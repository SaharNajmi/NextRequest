package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.collection.presentation.CollectionViewModel
import com.example.nextrequest.core.presentation.UiState
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: CollectionViewModel
    lateinit var collectionRepository: CollectionRepository

    private val sampleCollections = listOf(
        RequestCollection(collectionId = "12", collectionName = "library"),
        RequestCollection(collectionId = "13", collectionName = "book"),
        RequestCollection(collectionId = "14", collectionName = "dictionary"),
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        collectionRepository = mockk<CollectionRepository>(relaxed = true)
        viewModel = CollectionViewModel(collectionRepository, testDispatcher)
    }

    @AfterEach
    fun teardown() {
        Dispatchers.resetMain()
    }

    private fun successData() =
        (viewModel.uiState.value as UiState.Success).data

    @Test
    fun `initial state is Loading`() {
        viewModel.uiState.value.shouldBeInstanceOf<UiState.Loading>()
    }

    @Test
    fun `getCollections emits Success with mapped collections`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.getCollections()
        advanceUntilIdle()

        viewModel.uiState.value.shouldBeInstanceOf<UiState.Success<*>>()
        successData().size shouldBe 3
        successData()[0].requestCollection.collectionId shouldBe "12"
        successData()[1].requestCollection.collectionId shouldBe "13"
        successData()[2].requestCollection.collectionId shouldBe "14"
    }

    @Test
    fun `getCollections emits Error when repository throws`() = runTest {
        coEvery { collectionRepository.getAllCollections() } throws RuntimeException("db error")

        viewModel.getCollections()
        advanceUntilIdle()

        viewModel.uiState.value.shouldBeInstanceOf<UiState.Error>()
        (viewModel.uiState.value as UiState.Error).message shouldBe "db error"
    }

    @Test
    fun `getCollections preserves expanded state on reload`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.getCollections()
        advanceUntilIdle()

        viewModel.toggleExpanded("13")
        viewModel.getCollections()
        advanceUntilIdle()

        successData()[1].requestCollection.collectionId shouldBe "13"
        successData()[1].isExpanded shouldBe true
        successData()[0].isExpanded shouldBe false
        successData()[2].isExpanded shouldBe false
    }

    @Test
    fun `toggleExpanded flips isExpanded by given collectionId`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections
        viewModel.getCollections()
        advanceUntilIdle()

        viewModel.toggleExpanded("13")
        successData()[1].isExpanded shouldBe true

        viewModel.toggleExpanded("12")
        successData()[0].isExpanded shouldBe true

        viewModel.toggleExpanded("14")
        successData().shouldForAll { it.isExpanded.shouldBeTrue() }

        viewModel.toggleExpanded("12")
        successData()[0].isExpanded shouldBe false
    }

    @Test
    fun `deleteRequestItem calls repository then reloads collections`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.deleteRequestItem(requestId = 5)
        advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepository.deleteItemFromCollection(5) }
        coVerify(exactly = 1) { collectionRepository.getAllCollections() }
    }

    @Test
    fun `deleteCollection calls repository then reloads collections`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.deleteCollection("12")
        advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepository.deleteCollection("12") }
        coVerify(exactly = 1) { collectionRepository.getAllCollections() }
    }

    @Test
    fun `createNewCollection inserts a new collection then reloads`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.createNewCollection()
        advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepository.insertCollection(any()) }
        coVerify(exactly = 1) { collectionRepository.getAllCollections() }
    }

    @Test
    fun `createAnEmptyRequest inserts Http item then reloads`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.createAnEmptyRequest("12")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            collectionRepository.insertItemToCollection(
                "12",
                match { it is CollectionItem.Http && it.requestName == "Http Request" }
            )
        }
        coVerify(exactly = 1) { collectionRepository.getAllCollections() }
    }

    @Test
    fun `changeRequestName updates name then reloads`() = runTest {
        coEvery { collectionRepository.getAllCollections() } returns sampleCollections

        viewModel.changeRequestName(requestId = 7, requestName = "Renamed")
        advanceUntilIdle()

        coVerify(exactly = 1) { collectionRepository.changeRequestName(7, "Renamed") }
        coVerify(exactly = 1) { collectionRepository.getAllCollections() }
    }
}
