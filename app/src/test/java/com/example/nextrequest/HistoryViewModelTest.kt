package com.example.nextrequest

import com.example.nextrequest.history.domain.formatDate
import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.history.presentation.HistoryViewModel
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    lateinit var viewModel: HistoryViewModel
    private val testDispatcher = StandardTestDispatcher()
    lateinit var historyRepository: HistoryRepository
    lateinit var collectionRepository: CollectionRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        historyRepository = mockk<HistoryRepository>(relaxed = true)
        collectionRepository = mockk<CollectionRepository>(relaxed = true)
        viewModel = HistoryViewModel(historyRepository, collectionRepository, testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getHistories should group histories by date`() = runTest {
        val today = LocalDate.now()
        val yesterday = LocalDate.now().minusDays(1)
        val histories = listOf(
            History(id = 1, requestUrl = "url1", createdAt = today),
            History(id = 2, requestUrl = "url2", createdAt = yesterday),
            History(id = 3, requestUrl = "url3", createdAt = today),
        )

        coEvery { historyRepository.getAllHistories() } returns histories

        viewModel.getHistories()
//          testDispatcher.scheduler.advanceUntilIdle()
        advanceUntilIdle()
        val grouped = viewModel.historyEntry.value
        grouped.size shouldBe 2
        grouped[0].dateCreated shouldBe formatDate(today)
        grouped[1].dateCreated shouldBe formatDate(yesterday)

        grouped[0].histories.size shouldBe 2
        grouped[1].histories.size shouldBe 1

        val expanded = viewModel.expandedStates.value
        expanded.size shouldBe 2
    }

    @Test
    fun `toggleExpanded flips isExpanded for the selected date`() = runTest {
        val today = LocalDate.now()
        val yesterday = LocalDate.now().minusDays(1)
        val histories = listOf(
            History(id = 1, requestUrl = "url1", createdAt = today),
            History(id = 2, requestUrl = "url2", createdAt = yesterday),
            History(id = 3, requestUrl = "url3", createdAt = today),
        )

        coEvery { historyRepository.getAllHistories() } returns histories

        viewModel.getHistories()
        advanceUntilIdle()
        viewModel.toggleExpanded(formatDate(today))

        val expectedStateFirst = listOf(
            ExpandableHistoryItem(formatDate(today), isExpanded = true),
            ExpandableHistoryItem(formatDate(yesterday), isExpanded = false)
        )
        viewModel.expandedStates.value shouldBe expectedStateFirst

        viewModel.toggleExpanded(formatDate(yesterday))
        val expectedStateSecond = listOf(
            ExpandableHistoryItem(formatDate(today), isExpanded = true),
            ExpandableHistoryItem(formatDate(yesterday), isExpanded = true)
        )
        viewModel.expandedStates.value shouldBe expectedStateSecond

        viewModel.toggleExpanded(formatDate(today))
        val expectedStateThird = listOf(
            ExpandableHistoryItem(formatDate(today), isExpanded = false),
            ExpandableHistoryItem(formatDate(yesterday), isExpanded = true)
        )
        viewModel.expandedStates.value shouldBe expectedStateThird
    }

    @Test
    fun `getCollections should get collectionNames without duplication`() = runTest {
        val collections =
            listOf(
                Collection(collectionId = "56", collectionName = "c1"),
                Collection(collectionId = "23", collectionName = "c2"),
                Collection(collectionId = "23", collectionName = "c2"),
            )
        coEvery { collectionRepository.getAllCollections() } returns collections
        viewModel.getCollections()
        advanceUntilIdle()
        viewModel.collectionNames.value.size shouldBe 2
    }
}