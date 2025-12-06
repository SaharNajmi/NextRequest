package com.example.nextrequest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.history.data.repository.HistoryRepositoryImp
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.data.dao.HistoryRequestDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HistoryRepositoryImpTest {
    lateinit var db: AppDatabase
    lateinit var historyDao: HistoryRequestDao
    lateinit var historyRepository: HistoryRepositoryImp

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        historyDao = db.historyRequestDao()
        historyRepository = HistoryRepositoryImp(historyDao, Dispatchers.IO)
    }

    @After
    fun closeDb() {
        db.close()
    }

    private suspend fun createOneHistoryItem() {
        val item = History(requestUrl = "test.dev")
        historyRepository.insertHistoryRequest(item)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getAllHistories_returnsHistories() = runTest {
        historyRepository.insertHistoryRequest( History(requestUrl = "test.dev"))
        historyRepository.insertHistoryRequest( History(requestUrl = "test2"))
        val expected = historyRepository.getAllHistories()
        assertEquals(expected.size, 2)
    }

    @Test
    fun insertHistoryRequest_insertsHistoryIntoDatabase() = runTest {
        createOneHistoryItem()
        assertEquals(historyRepository.getAllHistories().size, 1)
    }

    @Test
    fun updateHistoryRequest_updatesExistingHistory() = runTest {
        val item = History(id = 2, requestUrl = "test.dev")
        historyRepository.insertHistoryRequest(item)
        historyRepository.updateHistoryRequest(item.copy(requestUrl = "updated url"))
        val expected = historyRepository.getAllHistories().first().requestUrl
        assertEquals(expected, "updated url")
    }

    @Test
    fun deleteHistoryRequest_deletesHistoryById() = runTest {
        val item = History(id = 2, requestUrl = "test.dev")
        historyRepository.insertHistoryRequest(item)
        historyRepository.deleteHistoryRequest(2)
        val expected = historyRepository.getAllHistories().size
        assertEquals(expected, 0)
    }

    @Test
    fun deleteHistoriesRequest_deletesMultipleHistories() = runTest {
        historyRepository.insertHistoryRequest(History(id = 11, requestUrl = "test 11"))
        historyRepository.insertHistoryRequest(History(id = 12, requestUrl = "test 12"))
        historyRepository.insertHistoryRequest(History(id = 13, requestUrl = "test 13"))
        historyRepository.deleteHistoriesRequest(listOf(11, 12, 13))
        val expected = historyRepository.getAllHistories().size
        assertEquals(expected, 0)
    }

    @Test
    fun getHistoryRequest_returnsHistoryById() = runTest {
        val historyItem = History(id = 11, requestUrl = "test 11")
        historyRepository.insertHistoryRequest(historyItem)
        val expected = historyRepository.getHistoryRequest(11)
        assertEquals(expected, historyItem)
    }
}