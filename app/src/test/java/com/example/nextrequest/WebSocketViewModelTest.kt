package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.presentation.navigation.Screens
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import com.example.nextrequest.socket.presentation.component.WebSocketViewModel
import com.example.nextrequest.socket.presentation.component.model.WebSocketUiModel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val messagesFlow = MutableSharedFlow<WebSocketMessage>()
    private val isConnectedFlow = MutableStateFlow(false)

    private lateinit var viewModel: WebSocketViewModel
    private lateinit var wsRepository: WebSocketRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var collectionRepository: CollectionRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        wsRepository = mockk(relaxed = true) {
            every { messages } returns messagesFlow
            every { isConnected } returns isConnectedFlow
        }
        historyRepository = mockk(relaxed = true)
        collectionRepository = mockk(relaxed = true)
        viewModel = WebSocketViewModel(wsRepository, historyRepository, collectionRepository, testDispatcher)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun successData() = (viewModel.uiState.value as UiState.Success).data

    @Test
    fun `initial state is Success with an empty disconnected model`() {
        viewModel.uiState.value.shouldBeInstanceOf<UiState.Success<*>>()
        successData() shouldBe WebSocketUiModel()
    }

    @Test
    fun `connect calls repository and updates url when isConnected emits true`() = runTest {
        viewModel.connect("ws://example.com")
        advanceUntilIdle()

        isConnectedFlow.value = true
        advanceUntilIdle()

        verify(exactly = 1) { wsRepository.connect("ws://example.com") }
        with(successData()) {
            isConnected shouldBe true
            url shouldBe "ws://example.com"
        }
    }

    @Test
    fun `connect emits Error when repository throws`() = runTest {
        every { wsRepository.connect(any()) } throws Exception("connection refused")

        viewModel.connect("ws://bad.url")
        advanceUntilIdle()

        viewModel.uiState.value.shouldBeInstanceOf<UiState.Error>()
        (viewModel.uiState.value as UiState.Error).message shouldBe "connection refused"
    }

    @Test
    fun `disconnect delegates to repository`() {
        viewModel.disconnect()
        verify(exactly = 1) { wsRepository.disconnect() }
    }

    @Test
    fun `incoming message is prepended to visibleMessages`() = runTest {
        messagesFlow.emit(WebSocketMessage("hello", isSentByUser = false, timestamp = 0L))
        advanceUntilIdle()

        with(successData().visibleMessages) {
            size shouldBe 1
            first().text shouldBe "hello"
            first().isSentByUser shouldBe false
        }
    }

    @Test
    fun `multiple incoming messages are prepended newest-first`() = runTest {
        messagesFlow.emit(WebSocketMessage("first", isSentByUser = false, timestamp = 0L))
        messagesFlow.emit(WebSocketMessage("second", isSentByUser = true, timestamp = 0L))
        advanceUntilIdle()

        with(successData().visibleMessages) {
            size shouldBe 2
            this[0].text shouldBe "second"
            this[1].text shouldBe "first"
        }
    }

    @Test
    fun `message flow error emits UiState Error`() = runTest {
        val silentConnectedFlow = MutableSharedFlow<Boolean>()
        val throwingRepository = mockk<WebSocketRepository>(relaxed = true) {
            every { messages } returns flow { throw Exception("stream error") }
            every { isConnected } returns silentConnectedFlow
        }
        val vm = WebSocketViewModel(throwingRepository, historyRepository, collectionRepository, testDispatcher)
        advanceUntilIdle()

        vm.uiState.value.shouldBeInstanceOf<UiState.Error>()
        (vm.uiState.value as UiState.Error).message shouldBe "stream error"
    }

    @Test
    fun `sendMessage delegates to repository`() = runTest {
        viewModel.sendMessage("ping")
        advanceUntilIdle()

        verify(exactly = 1) { wsRepository.sendMessage("ping") }
    }

    @Test
    fun `sendMessage emits Error when repository throws`() = runTest {
        every { wsRepository.sendMessage(any()) } throws Exception("send failed")

        viewModel.sendMessage("ping")
        advanceUntilIdle()

        viewModel.uiState.value.shouldBeInstanceOf<UiState.Error>()
        (viewModel.uiState.value as UiState.Error).message shouldBe "send failed"
    }

    @Test
    fun `hideMessages moves all visibleMessages to hiddenMessages`() = runTest {
        messagesFlow.emit(WebSocketMessage("msg1", false, 0L))
        messagesFlow.emit(WebSocketMessage("msg2", true, 0L))
        advanceUntilIdle()

        viewModel.hideMessages()

        with(successData()) {
            visibleMessages shouldBe emptyList()
            hiddenMessages.size shouldBe 2
        }
    }

    @Test
    fun `hideMessages called twice accumulates all messages as hidden`() = runTest {
        messagesFlow.emit(WebSocketMessage("msg1", false, 0L))
        advanceUntilIdle()
        viewModel.hideMessages()

        messagesFlow.emit(WebSocketMessage("msg2", true, 0L))
        advanceUntilIdle()
        viewModel.hideMessages()

        with(successData()) {
            visibleMessages shouldBe emptyList()
            hiddenMessages.size shouldBe 2
        }
    }

    @Test
    fun `showHiddenMessages moves hiddenMessages back to visibleMessages`() = runTest {
        messagesFlow.emit(WebSocketMessage("msg1", false, 0L))
        advanceUntilIdle()
        viewModel.hideMessages()

        viewModel.showHiddenMessages()

        with(successData()) {
            hiddenMessages shouldBe emptyList()
            visibleMessages.size shouldBe 1
            visibleMessages.first().text shouldBe "msg1"
        }
    }

    @Test
    fun `showHiddenMessages appends hidden messages after existing visible messages`() = runTest {
        messagesFlow.emit(WebSocketMessage("old", false, 0L))
        advanceUntilIdle()
        viewModel.hideMessages()

        messagesFlow.emit(WebSocketMessage("new", true, 0L))
        advanceUntilIdle()

        viewModel.showHiddenMessages()

        with(successData()) {
            hiddenMessages shouldBe emptyList()
            visibleMessages.size shouldBe 2
            visibleMessages[0].text shouldBe "new"
            visibleMessages[1].text shouldBe "old"
        }
    }

    @Test
    fun `disconnecting saves history with both visible and hidden messages`() = runTest {
        viewModel.connect("ws://example.com")
        isConnectedFlow.value = true
        advanceUntilIdle()

        messagesFlow.emit(WebSocketMessage("hidden msg", false, 0L))
        advanceUntilIdle()
        viewModel.hideMessages()

        messagesFlow.emit(WebSocketMessage("visible msg", true, 0L))
        advanceUntilIdle()

        isConnectedFlow.value = false
        advanceUntilIdle()

        coVerify(exactly = 1) {
            historyRepository.insertHistory(match { item ->
                item is HistoryItem.WebSocket &&
                        item.request.url == "ws://example.com" &&
                        item.request.messages.size == 2
            })
        }
    }

    @Test
    fun `no history is saved when isConnected goes false without a prior connect`() = runTest {
        isConnectedFlow.value = false
        advanceUntilIdle()

        coVerify(exactly = 0) { historyRepository.insertHistory(any()) }
    }

    @Test
    fun `no history is saved when url was never set`() = runTest {
        isConnectedFlow.value = true
        advanceUntilIdle()

        isConnectedFlow.value = false
        advanceUntilIdle()

        coVerify(exactly = 0) { historyRepository.insertHistory(any()) }
    }

    @Test
    fun `loadRequest from history loads url and messages`() = runTest {
        val wsRequest = WebSocketRequest(
            url = "ws://history.com",
            messages = listOf(
                WebSocketMessage("h1", false, 0L),
                WebSocketMessage("h2", true, 0L),
            )
        )
        coEvery { historyRepository.getHistory(1) } returns HistoryItem.WebSocket(id = 1, request = wsRequest)

        viewModel.loadRequest(requestId = 1, source = Screens.ROUTE_HISTORY_SCREEN)
        advanceUntilIdle()

        with(successData()) {
            url shouldBe "ws://history.com"
            visibleMessages.size shouldBe 2
            isConnected shouldBe false
        }
    }

    @Test
    fun `loadRequest from collection loads url and messages`() = runTest {
        val wsRequest = WebSocketRequest(
            url = "ws://collection.com",
            messages = listOf(WebSocketMessage("c1", false, 0L))
        )
        coEvery { collectionRepository.getCollectionItem(5) } returns CollectionItem.WebSocket(
            requestId = 5, requestName = "My Socket", request = wsRequest
        )

        viewModel.loadRequest(requestId = 5, source = Screens.ROUTE_COLLECTION_SCREEN)
        advanceUntilIdle()

        with(successData()) {
            url shouldBe "ws://collection.com"
            visibleMessages.size shouldBe 1
        }
    }

    @Test
    fun `loadRequest does nothing when history item is Http`() = runTest {
        coEvery { historyRepository.getHistory(99) } returns HistoryItem.Http(
            id = 99, request = HttpRequest(requestUrl = "http://example.com")
        )

        val urlBefore = successData().url
        viewModel.loadRequest(requestId = 99, source = Screens.ROUTE_HISTORY_SCREEN)
        advanceUntilIdle()

        successData().url shouldBe urlBefore
    }

    @Test
    fun `loadRequest does nothing for unknown source`() = runTest {
        val urlBefore = successData().url
        viewModel.loadRequest(requestId = 1, source = "unknown_source")
        advanceUntilIdle()

        successData().url shouldBe urlBefore
    }

    @Test
    fun `loadRequest clears hiddenMessages to prevent duplicates on show messages`() = runTest {
        val wsRequest = WebSocketRequest(
            url = "ws://example.com",
            messages = listOf(WebSocketMessage("msg", false, 0L))
        )
        coEvery { historyRepository.getHistory(1) } returns HistoryItem.WebSocket(id = 1, request = wsRequest)

        viewModel.loadRequest(1, Screens.ROUTE_HISTORY_SCREEN)
        advanceUntilIdle()
        viewModel.hideMessages()

        viewModel.loadRequest(1, Screens.ROUTE_HISTORY_SCREEN)
        advanceUntilIdle()

        viewModel.showHiddenMessages()

        successData().visibleMessages.size shouldBe 1
    }
}
