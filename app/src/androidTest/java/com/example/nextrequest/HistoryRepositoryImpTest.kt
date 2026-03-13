package com.example.nextrequest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.history.data.dao.HistoryDao
import com.example.nextrequest.history.data.model.HttpRequest
import com.example.nextrequest.history.data.model.WebSocketRequest
import com.example.nextrequest.history.data.repository.HistoryRepositoryImp
import com.example.nextrequest.history.domain.model.HistoryItem
import kotlinx.coroutines.Dispatchers
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

    private fun httpHistory(
        id: Int = 1,
        url: String = "test.dev",
    ) = HistoryItem.Http(
        HttpRequest(
            id = id,
            requestUrl = url
        )
    )

    private fun webSocketHistory(
        id: Int = 2,
        url: String = "ws://test.dev",
    ) = HistoryItem.WebSocket(
        WebSocketRequest(
            id = id,
            url = url,
            createdAt = System.currentTimeMillis()
        )
    )


    @Test
    fun insertHistory_insertsHistoryHttpIntoDatabase() = runTest {
        historyRepository.insertHistory(httpHistory())
        assertEquals(1, historyRepository.getAllHistories().size)
    }

    @Test
    fun insertHistory_insertsWebSocketIntoDatabase() = runTest {
        historyRepository.insertHistory(webSocketHistory())
        assertEquals(1, historyRepository.getAllHistories().size)
    }

    @Test
    fun updateHistory_updatesExistingWebSocketHistory() = runTest {
        val item = webSocketHistory(12)
        historyRepository.insertHistory(item)
        val updated = item.copy(request = item.request.copy(url = "ws://updated.dev"))
        historyRepository.updateHistory(updated)
        val result = historyRepository.getAllHistories().first() as HistoryItem.WebSocket

        assertEquals("ws://updated.dev", result.request.url)
    }

    @Test
    fun updateHistory_updatesExistingHttpRequestHistory() = runTest {
        val item = httpHistory(10)
        historyRepository.insertHistory(item)
        val updated = item.copy(request = item.request.copy(requestUrl = "ws://updated.dev"))
        historyRepository.updateHistory(updated)
        val result = historyRepository.getAllHistories().first() as HistoryItem.Http


        assertEquals("ws://updated.dev", result.request.requestUrl)
    }

    @Test
    fun getAllHistories_returnsHttpAndWebSocket() = runTest {
        historyRepository.insertHistory(httpHistory(id = 1))
        historyRepository.insertHistory(webSocketHistory(id = 2))
        val result = historyRepository.getAllHistories()

        assertEquals(2, result.size)
    }

    @Test
    fun getHistory_returnsWebSocketHistoryById() = runTest {
        val item = webSocketHistory(id = 20)
        historyRepository.insertHistory(item)
        val result = historyRepository.getHistory(20)

        assertEquals(item, result)
    }

    @Test
    fun deleteHistory_deletesHistoryById() = runTest {
        val item = HistoryItem.Http(HttpRequest(id = 2, requestUrl = "test.dev"))
        historyRepository.insertHistory(item)
        historyRepository.deleteHistory(2)
        val expected = historyRepository.getAllHistories().size
        assertEquals(expected, 0)
    }

    @Test
    fun deleteHistories_deletesMultipleHistories() = runTest {
        historyRepository.insertHistory(httpHistory(11))
        historyRepository.insertHistory(webSocketHistory(12))
        historyRepository.insertHistory(httpHistory(13))
        historyRepository.deleteHistories(listOf(11, 12, 13))
        val result = historyRepository.getAllHistories().size
        assertEquals(0, result)
    }

    @Test
    fun deleteHistories_deletesOnlySpecifiedIds() = runTest {
        historyRepository.insertHistory(httpHistory(11))
        historyRepository.insertHistory(webSocketHistory(12))
        historyRepository.insertHistory(httpHistory(13))
        historyRepository.insertHistory(httpHistory(99))

        historyRepository.deleteHistories(listOf(11, 12, 13))

        val result = historyRepository.getAllHistories()

        assertEquals(1, result.size)
    }

    @Test
    fun getHistory_returnsHistoryById() = runTest {
        val historyItem = HistoryItem.Http(HttpRequest(id = 11, requestUrl = "test 11"))
        historyRepository.insertHistory(historyItem)
        val expected = historyRepository.getHistory(11)
        assertEquals(expected, historyItem)
    }

    @Test
    fun getHistory_returnsHttpAndWebSocketById() = runTest {
        val http = httpHistory(11)
        val webSocket = webSocketHistory(12)
        historyRepository.insertHistory(http)
        historyRepository.insertHistory(webSocket)

        val httpResult = historyRepository.getHistory(11)
        val webSocketResult = historyRepository.getHistory(12)

        assert(httpResult is HistoryItem.Http)
        assert(webSocketResult is HistoryItem.WebSocket)

        assertEquals(http, httpResult)
        assertEquals(webSocket, webSocketResult)
    }
}