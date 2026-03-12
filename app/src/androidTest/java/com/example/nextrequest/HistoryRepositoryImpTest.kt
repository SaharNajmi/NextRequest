package com.example.nextrequest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.history.data.repository.HistoryRepositoryImp
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.data.dao.HistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HistoryRepositoryImpTest {
    lateinit var db: AppDatabase
    lateinit var historyDao: HistoryDao
    lateinit var historyRepository: HistoryRepositoryImp

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        historyDao = db.historyDao()
        historyRepository = HistoryRepositoryImp(historyDao, Dispatchers.IO)
    }

    @After
    fun closeDb() {
        db.close()
    }

    private suspend fun createOneHistoryItem() {
        val item = History(requestUrl = "test.dev")
        historyRepository.insertHistoryHttp(item)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllHistories_returnsHistories() = runTest {
        historyRepository.insertHistoryHttp( History(requestUrl = "test.dev"))
        historyRepository.insertHistoryHttp( History(requestUrl = "test2"))
        val expected = historyRepository.getAllHistories()
        assertEquals(expected.size, 2)
    }

    @Test
    fun insertHistory_insertsHistoryHttpIntoDatabase() = runTest {
        createOneHistoryItem()
        assertEquals(historyRepository.getAllHistories().size, 1)
    }

    @Test
    fun updateHistory_updatesExistingHistory() = runTest {
        val item = History(id = 2, requestUrl = "test.dev")
        historyRepository.insertHistoryHttp(item)
        historyRepository.updateHistory(item.copy(requestUrl = "updated url"))
        val expected = historyRepository.getAllHistories().first().requestUrl
        assertEquals(expected, "updated url")
    }

    @Test
    fun deleteHistory_deletesHistoryById() = runTest {
        val item = History(id = 2, requestUrl = "test.dev")
        historyRepository.insertHistoryHttp(item)
        historyRepository.deleteHistory(2)
        val expected = historyRepository.getAllHistories().size
        assertEquals(expected, 0)
    }

    @Test
    fun deleteHistories_deletesMultipleHistories() = runTest {
        historyRepository.insertHistoryHttp(History(id = 11, requestUrl = "test 11"))
        historyRepository.insertHistoryHttp(History(id = 12, requestUrl = "test 12"))
        historyRepository.insertHistoryHttp(History(id = 13, requestUrl = "test 13"))
        historyRepository.deleteHistories(listOf(11, 12, 13))
        val expected = historyRepository.getAllHistories().size
        assertEquals(expected, 0)
    }

    @Test
    fun getHistory_returnsHistoryById() = runTest {
        val historyItem = History(id = 11, requestUrl = "test 11")
        historyRepository.insertHistoryHttp(historyItem)
        val expected = historyRepository.getHistory(11)
        assertEquals(expected, historyItem)
    }
}