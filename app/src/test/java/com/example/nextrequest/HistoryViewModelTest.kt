package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.data.extensions.toLong
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.history.domain.formatDate
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.history.presentation.HistoryViewModel
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    lateinit var viewModel: HistoryViewModel
    private val testDispatcher = StandardTestDispatcher()
    lateinit var historyRepository: HistoryRepository
    lateinit var collectionRepository: CollectionRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        historyRepository = mockk<HistoryRepository>(relaxed = true)
        collectionRepository = mockk<CollectionRepository>(relaxed = true)
        viewModel = HistoryViewModel(historyRepository, collectionRepository, testDispatcher)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getHistories should group histories by date`() = runTest {
        val today = LocalDate.now()
        val yesterday = LocalDate.now().minusDays(1)
        val histories = listOf(
            HistoryItem.Http(id = 1, HttpRequest(requestUrl = "url1", createdAt = today.toLong())),
            HistoryItem.Http(
                id = 2,
                HttpRequest(requestUrl = "url2", createdAt = yesterday.toLong())
            ),
            HistoryItem.WebSocket(
                id = 3,
                WebSocketRequest(url = "url3", createdAt = today.toLong())
            ),
        )

        coEvery { historyRepository.getAllHistories() } returns histories

        viewModel.getHistories()
        advanceUntilIdle()
        with((viewModel.uiState.value as UiState.Success).data) {
            historyEntries.size shouldBe 2
            historyEntries[0].dateCreated shouldBe formatDate(today)
            historyEntries[1].dateCreated shouldBe formatDate(yesterday)

            historyEntries[0].histories.size shouldBe 2
            historyEntries[1].histories.size shouldBe 1

            expandedStates.size shouldBe 2
            expandedStates.forAll { it.isExpanded shouldBe false }
        }
    }

    @Test
    fun `toggleExpanded flips isExpanded for the selected date`() = runTest {
        val today = LocalDate.now()
        val yesterday = LocalDate.now().minusDays(1)
        val todayFormatted = formatDate(today)
        val yesterdayFormatted = formatDate(yesterday)

        val histories = listOf(
            HistoryItem.Http(id = 1, HttpRequest(requestUrl = "url1", createdAt = today.toLong())),
            HistoryItem.Http(
                id = 2, HttpRequest(
                    requestUrl = "url2",
                    createdAt = yesterday.toLong()
                )
            ),
            HistoryItem.WebSocket(
                id = 3, WebSocketRequest(
                    url = "url3",
                    createdAt = today.toLong()
                )
            ),
        )

        coEvery { historyRepository.getAllHistories() } returns histories

        viewModel.getHistories()
        advanceUntilIdle()

        viewModel.toggleExpanded(todayFormatted)
        (viewModel.uiState.value as UiState.Success).data.expandedStates shouldBe listOf(
            ExpandableHistoryItem(todayFormatted, isExpanded = true),
            ExpandableHistoryItem(yesterdayFormatted, isExpanded = false)
        )

        viewModel.toggleExpanded(yesterdayFormatted)
        (viewModel.uiState.value as UiState.Success).data.expandedStates shouldBe listOf(
            ExpandableHistoryItem(todayFormatted, isExpanded = true),
            ExpandableHistoryItem(yesterdayFormatted, isExpanded = true)
        )

        viewModel.toggleExpanded(todayFormatted)
        (viewModel.uiState.value as UiState.Success).data.expandedStates shouldBe listOf(
            ExpandableHistoryItem(todayFormatted, isExpanded = false),
            ExpandableHistoryItem(yesterdayFormatted, isExpanded = true)
        )
    }

    @Test
    fun `getCollections should get collectionNames without duplication`() = runTest {
        val requestCollections =
            listOf(
                RequestCollection(collectionId = "56", collectionName = "c1"),
                RequestCollection(collectionId = "23", collectionName = "c2"),
                RequestCollection(collectionId = "23", collectionName = "c2"),
            )
        coEvery { collectionRepository.getAllCollections() } returns requestCollections
        viewModel.getCollections()
        advanceUntilIdle()
        val uiState = viewModel.uiState.value as UiState.Success
        uiState.data.collectionNames.size shouldBe 2
    }
}