package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.collection.presentation.CollectionViewModel
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: CollectionViewModel
    lateinit var collectionRepository: CollectionRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        collectionRepository = mockk<CollectionRepository>()
        viewModel = CollectionViewModel(collectionRepository, testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleExpanded flips isExpanded by given collectionId`() = runTest {
        val collections = listOf(
            Collection(collectionId = "12", collectionName = "library"),
            Collection(collectionId = "13", collectionName = "book"),
            Collection(collectionId = "14", collectionName = "dictionary"),
        )

        coEvery { collectionRepository.getAllCollections() } returns collections
        viewModel.getCollections()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleExpanded("13")
        viewModel.collections.value[1].isExpanded shouldBe true

        viewModel.toggleExpanded("12")
        viewModel.collections.value[0].isExpanded shouldBe true

        viewModel.toggleExpanded("14")
        viewModel.collections.value.shouldForAll { it.isExpanded.shouldBeTrue() }
//        viewModel.collections.value.forEach { it.isExpanded shouldBe true }

        viewModel.toggleExpanded("12")
        viewModel.collections.value[0].isExpanded shouldBe false
    }

}