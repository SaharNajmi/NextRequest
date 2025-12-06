package com.example.nextrequest

import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.domain.repository.ApiService
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.home.presentation.Loadable
import com.example.nextrequest.home.presentation.HomeUiState
import com.example.nextrequest.home.presentation.HomeViewModel
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: HomeViewModel
    lateinit var historyRepo: HistoryRepository
    lateinit var apiService: ApiService
    lateinit var collectionRepo: CollectionRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk<ApiService>()
        historyRepo = mockk<HistoryRepository>(relaxed = true)
        collectionRepo = mockk<CollectionRepository>(relaxed = true)

        viewModel = HomeViewModel(apiService, historyRepo, collectionRepo, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendRequest should save response to history`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        viewModel.sendRequest()
        // viewModel.uiState.value.response.shouldBeTypeOf<Loadable.Loading>
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { historyRepo.insertHistoryRequest(any()) }
    }

    @Test
    fun `sendRequest should updates collection if collectionId is provided`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        viewModel.sendRequest(collectionId = "1232")
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) {
            collectionRepo.updateCollectionRequest(
                "1232",
                any<Request>()
            )
        }
    }

    @Test
    fun `sendRequest should not update collection if collectionId is null`() {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        viewModel.sendRequest(collectionId = null)

        coVerify(exactly = 0) {
            collectionRepo.updateCollectionRequest(
                any<String>(),
                any<Request>()
            )
        }

    }

    @Test
    fun `sendRequest should not call repository when requestUrl is empty`() {
        viewModel.sendRequest()

        coVerify(exactly = 0) {
            apiService.sendRequest(any<String>(), any<String>())
            historyRepo.insertHistoryRequest(any<History>())
            collectionRepo.updateCollectionRequest(
                any<String>(),
                any<Request>()
            )
        }
    }

    @Test
    fun `sendRequest should call repository when requestUrl is not empty`() = runTest {
        viewModel.updateHttpMethod(HttpMethod.GET)
        viewModel.updateRequestUrl("http://example.com")
        viewModel.sendRequest()
        testDispatcher.scheduler.advanceUntilIdle()//or advanceUntilIdle()
        coVerify(exactly = 1) {
            apiService.sendRequest(
                any<String>(),
                any<String>(),
                any<KeyValueList>(),
                any<KeyValueList>(),
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
        viewModel.uiState.value.data.headers?.get(0)?.first shouldBe key
        viewModel.uiState.value.data.headers?.get(0)?.second shouldBe "Bearer $value"

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

        viewModel.uiState.value.data.headers?.get(0)?.second shouldBe "Bearer anotherToken"
        viewModel.uiState.value.data.headers?.size shouldBe 3
    }

    @Test
    fun `addHeader should update uiState`() {
        val key = "Content-Type"
        val value = "application/json"
        viewModel.addHeader(key, value)

        viewModel.uiState.value.data.headers?.get(0) shouldBe Pair(key, value)
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
        viewModel.uiState.value.data.params?.get(0) shouldBe Pair(key, value)
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
            History(requestUrl = "/test", statusCode = 200, response = "url response")
//        every { savedRequest.statusCode } returns 200
//        every { savedRequest.toHttpResponse() } answers { ApiResponse("url response", 200) }
//        every { savedRequest.toHttpRequest() } answers { ApiRequest(requestUrl = "/test") }
        coEvery { historyRepo.getHistoryRequest(1) } returns savedRequest

        viewModel.loadRequestFromHistory(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { historyRepo.getHistoryRequest(1) }
        (viewModel.uiState.first().response as Loadable.Success).data.statusCode shouldBe 200
    }

    @Test
    fun `loadRequestFromHistory returns error when statusCode is null`() = runTest {
        val savedRequest = mockk<History>(relaxed = true)
        every { savedRequest.statusCode } returns null
        every { savedRequest.requestUrl } returns "/test"
        every { savedRequest.response } returns "error"
        coEvery { historyRepo.getHistoryRequest(any<Int>()) } returns savedRequest

        viewModel.loadRequestFromHistory(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { historyRepo.getHistoryRequest(any<Int>()) }

        viewModel.uiState.first().response shouldBe Loadable.Error("error")
        (viewModel.uiState.first().response as? Loadable.Success)?.data?.statusCode shouldBe null
        (viewModel.uiState.first().response as Loadable.Error).message shouldBe "error"
    }

    @Test
    fun `loadRequestFromCollection returns success when statusCode is not null`() = runTest {
        val savedRequest =
            Request(requestUrl = "/test", statusCode = 200, response = "url response")
        coEvery { collectionRepo.getCollectionRequest(1) } returns savedRequest

        viewModel.loadRequestFromCollection(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepo.getCollectionRequest(1) }
        (viewModel.uiState.first().response as Loadable.Success).data.statusCode shouldBe 200
    }

    @Test
    fun `loadRequestFromCollection returns error when statusCode is null`() = runTest {
        val savedRequest = mockk<Request>(relaxed = true)
        every { savedRequest.statusCode } returns null
        every { savedRequest.requestUrl } returns "/test"
        every { savedRequest.response } returns "error"
        coEvery { collectionRepo.getCollectionRequest(any<Int>()) } returns savedRequest

        viewModel.loadRequestFromCollection(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { collectionRepo.getCollectionRequest(any<Int>()) }

        viewModel.uiState.first().response shouldBe Loadable.Error("error")
        (viewModel.uiState.first().response as? Loadable.Success)?.data?.statusCode shouldBe null
        (viewModel.uiState.first().response as Loadable.Error).message shouldBe "error"
    }

    @Test
    fun `loadRequestFromCollection returns empty result when requestUrl is null`() = runTest {
        val savedRequest = mockk<Request>(relaxed = true)
        every { savedRequest.requestUrl } returns null
        every { savedRequest.statusCode } returns null
        coEvery { collectionRepo.getCollectionRequest(any<Int>()) } returns savedRequest

        viewModel.loadRequestFromCollection(1)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify(exactly = 1) { collectionRepo.getCollectionRequest(any<Int>()) }

        viewModel.uiState.first().response shouldBe Loadable.Empty
    }

}