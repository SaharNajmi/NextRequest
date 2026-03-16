package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.models.KeyValue
import com.example.nextrequest.core.presentation.navigation.Screens
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.home.domain.repository.HomeRepository
import com.example.nextrequest.home.presentation.HomeUiState
import com.example.nextrequest.home.presentation.HomeViewModel
import com.example.nextrequest.home.presentation.Loadable
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: HomeViewModel
    lateinit var historyRepo: HistoryRepository
    lateinit var homeRepo: HomeRepository
    lateinit var collectionRepo: CollectionRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        historyRepo = mockk<HistoryRepository>(relaxed = true)
        homeRepo = mockk<HomeRepository>(relaxed = true)
        collectionRepo = mockk<CollectionRepository>(relaxed = true)

        viewModel = HomeViewModel(homeRepo, historyRepo, collectionRepo, testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendRequest should save response to history`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        coEvery { homeRepo.sendRequest(any(), any(), any(), any(), any<Any>()) }
        viewModel.sendRequest()
        // viewModel.uiState.value.response.shouldBeTypeOf<Loadable.Loading>
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { historyRepo.insertHistory(any()) }
    }

    @Test
    fun `sendRequest should updates collection if collectionId is provided`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        coEvery { homeRepo.sendRequest(any(), any(), any(), any(), any<Any>()) }
        viewModel.sendRequest(collectionId = "1232")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) {
            collectionRepo.updateCollectionItem(
                "1232",
                any<CollectionItem>()
            )
        }
    }

    @Test
    fun `sendRequest should not update collection if collectionId is null`() {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        viewModel.sendRequest(collectionId = null)

        coVerify(exactly = 0) {
            collectionRepo.updateCollectionItem(
                any<String>(),
                any<CollectionItem>()
            )
        }

    }

    @Test
    fun `sendRequest should not call repository when requestUrl is empty`() {
        viewModel.sendRequest()

        coVerify(exactly = 0) {
            homeRepo.sendRequest(any<String>(), any<String>())
            historyRepo.insertHistory(any<HistoryItem>())
            collectionRepo.updateCollectionItem(
                any<String>(),
                any<CollectionItem>()
            )
        }
    }

    @Test
    fun `sendRequest should calnnl repository when requestUrl is not empty`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        coEvery { homeRepo.sendRequest(any(), any(), any(), any(), any<Any>()) }

        viewModel.sendRequest()
        testDispatcher.scheduler.advanceUntilIdle()//or advanceUntilIdle()
        coVerify(exactly = 1) {
            homeRepo.sendRequest(
                any<String>(),
                any<String>(),
                any<List<KeyValue>>(),
                any<List<KeyValue>>(),
                any<Any>(),
            )
        }
    }

    @Test
    fun `clearData should reset uiState`() {
        viewModel.clearData()
        val expected = HomeUiState(ApiRequest(), Loadable.Empty)
        viewModel.uiState.value.equals(expected)
    }

    @Test
    fun `updateRequestUrl should update requestUrl in uiState`() {
        viewModel.updateRequestUrl("newUrl")
        viewModel.uiState.value.data.requestUrl.shouldBe("newUrl")
    }

    @Test
    fun `updateHttpMethod should update httpMethod in uiState`() {
        viewModel.updateHttpMethod(HttpMethod.PUT)
        viewModel.uiState.value.data.httpMethod.shouldBe(HttpMethod.PUT)
    }

    @Test
    fun `updateBody should update body in uiState`() {
        viewModel.updateBody("newBody")
        viewModel.uiState.value.data.body.shouldBe("newBody")
    }

    @Test
    fun `addHeader should add Bearer key at the beginning of header value if key is Authorization`() {
        val key = "Authorization"
        val value = "token"
        viewModel.addHeader(key, value)
        viewModel.uiState.value.data.headers?.get(0)?.key shouldBe key
        viewModel.uiState.value.data.headers?.get(0)?.value shouldBe "Bearer $value"

        viewModel.addHeader(key, value)
        viewModel.uiState.value.data.headers?.size shouldBe 1
    }

    @Test
    fun `addHeader shouldn't add duplicate key for Authorization`() {
        val key = "Authorization"
        val value = "token"
        viewModel.addHeader(key, value)
        viewModel.addHeader(key, "anotherToken")
        viewModel.addHeader("Set-Cookie", "SESSIONID=abc123")
        viewModel.addHeader("Set-Cookie", "USER_PREFS=dark_mode")

        viewModel.uiState.value.data.headers?.get(0)?.value shouldBe "Bearer anotherToken"
        viewModel.uiState.value.data.headers?.size shouldBe 3
    }

    @Test
    fun `addHeader should update uiState`() {
        val key = "Content-Type"
        val value = "application/json"
        viewModel.addHeader(key, value)

        viewModel.uiState.value.data.headers?.get(0) shouldBe KeyValue(key, value)
    }

    @Test
    fun `removeHeader should remove header from uiState`() {
        val key = "Content-Type"
        val value = "application/json"
        viewModel.addHeader(key, value)
        viewModel.addHeader("Set-Cookie", "SESSIONID=abc123")
        viewModel.removeHeader(key, value)

        viewModel.uiState.value.data.headers?.size shouldBe 1
    }

    @Test
    fun `addParameter should update param in uiState`() {
        val key = "id"
        val value = "21"
        viewModel.addParameter(key, value)

        viewModel.uiState.value.data.params?.size shouldBe 1
        viewModel.uiState.value.data.params?.get(0) shouldBe KeyValue(key, value)
    }


    @Test
    fun `removeParameter should update params in uiState`() {
        viewModel.addParameter("id", "89")
        viewModel.uiState.value.data.params?.size shouldBe 1
        viewModel.removeParameter("id", "89")
        viewModel.uiState.value.data.params?.size shouldBe 0
    }

    @Test
    fun `loadRequestFromHistory returns success when statusCode is not null`() = runTest {
        val savedRequest =
            HistoryItem.Http(
                id = 1,
                HttpRequest(requestUrl = "/test", statusCode = 200, response = "url response")
            )
        coEvery { historyRepo.getHistory(1) } returns savedRequest

        viewModel.loadRequest(1, Screens.ROUTE_HISTORY_SCREEN)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { historyRepo.getHistory(1) }
        (viewModel.uiState.first().response as Loadable.Success).data.statusCode shouldBe 200
    }

    @Test
    fun `loadRequestFromHistory returns error when statusCode is null`() = runTest {
        val savedRequest = HistoryItem.Http(
            id = 1, HttpRequest(
                requestUrl = "/test",
                response = "error",
                statusCode = null
            )
        )
        coEvery { historyRepo.getHistory(any<Int>()) } returns savedRequest

        viewModel.loadRequest(1, Screens.ROUTE_HISTORY_SCREEN)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { historyRepo.getHistory(any<Int>()) }

        viewModel.uiState.first().response shouldBe Loadable.Error("error")

        (viewModel.uiState.first().response as? Loadable.Success)?.data?.statusCode shouldBe null
        (viewModel.uiState.first().response as Loadable.Error).message shouldBe "error"
    }
    @Test
    fun `loadRequestFromCollection returns success when statusCode is not null`() = runTest {
        val savedCollectionItem = CollectionItem.Http(
            requestId = 1,
            requestName = "test request",
            request = HttpRequest(requestUrl = "/test", statusCode = 200, response = "url response")
        )
        coEvery { collectionRepo.getCollectionItem(1) } returns savedCollectionItem

        viewModel.loadRequest(1, Screens.ROUTE_COLLECTION_SCREEN)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepo.getCollectionItem(1) }
        (viewModel.uiState.first().response as Loadable.Success).data.statusCode shouldBe 200
    }

    @Test
    fun `loadRequestFromCollection returns error when statusCode is null`() = runTest {
        val savedCollectionItem = CollectionItem.Http(
            requestId = 1,
            requestName = "test request",
            request = HttpRequest(requestUrl = "/test", statusCode = null, response = "error")
        )
        coEvery { collectionRepo.getCollectionItem(1) } returns savedCollectionItem

        viewModel.loadRequest(1, Screens.ROUTE_COLLECTION_SCREEN)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepo.getCollectionItem(1) }
        (viewModel.uiState.first().response as Loadable.Error).message shouldBe "error"
    }

    @Test
    fun `checkValidUrl returns true when url is valid`() {
        viewModel.checkValidUrl("https://example.com").shouldBe(true)
        viewModel.checkValidUrl("http://example.com/path").shouldBe(true)
        viewModel.checkValidUrl("https://sub.domain.com").shouldBe(true)
    }

    @Test
    fun `checkValidUrl returns false when url is invalid`() {
        viewModel.checkValidUrl("invalid-url").shouldBe(false)
        viewModel.checkValidUrl("test .com").shouldBe(false)
        viewModel.checkValidUrl("htp:/example").shouldBe(false)
    }

    @Test
    fun `checkValidUrl sets error state for invalid urls`() {
        val actual = viewModel.checkValidUrl("bad-url")
        actual shouldBe false
        viewModel.uiState.value.response shouldBe
                Loadable.NetworkError("Please enter a valid URL")
    }

    @Test
    fun `checkValidUrl does not change state for valid urls`() {
        val actual = viewModel.checkValidUrl("https://example.com")
        actual shouldBe true
        viewModel.uiState.value.response shouldNotBe
                Loadable.NetworkError("Please enter a valid URL")
    }

}