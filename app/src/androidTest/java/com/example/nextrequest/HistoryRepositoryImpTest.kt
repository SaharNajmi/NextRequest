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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryRepositoryImpTest {
    lateinit var db: AppDatabase
    lateinit var historyDao: HistoryDao
    lateinit var historyRepository: HistoryRepositoryImp
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun createDb() {
        Dispatchers.setMain(dispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        historyDao = db.historyDao()
        historyRepository = HistoryRepositoryImp(historyDao, dispatcher)
    }

    @After
    fun closeDb() {
        db.close()
    }

    private fun httpHistory(
        id: Int = 1,
        url: String = "test.dev",
    ) = HistoryItem.Http(
        id = id, HttpRequest(
            requestUrl = url
        )
    )

    private fun webSocketHistory(
        id: Int = 2,
        url: String = "ws://test.dev",
    ) = HistoryItem.WebSocket(
        id = id, WebSocketRequest(
            url = url,
            createdAt = System.currentTimeMillis()
        )
    )


    @Test
    fun insertHistory_savesHttpRequestHistory() = runTest {
        historyRepository.insertHistory(httpHistory())
        assertEquals(1, historyRepository.getAllHistories().size)
    }

    @Test
    fun insertHistory_savesWebSocketHistory() = runTest {
        historyRepository.insertHistory(webSocketHistory())
        assertEquals(1, historyRepository.getAllHistories().size)
    }

    @Test
    fun updateHistory_modifiesStoredWebSocketRequest() = runTest {
        historyRepository.insertHistory(webSocketHistory())

        val inserted = historyRepository.getAllHistories().first() as HistoryItem.WebSocket

        val updated = inserted.copy(
            request = inserted.request.copy(url = "ws://updated.dev")
        )

        historyRepository.updateHistory(updated)

        val result = historyRepository.getAllHistories().first() as HistoryItem.WebSocket

        assertEquals("ws://updated.dev", result.request.url)
    }

    @Test
    fun updateHistory_modifiesStoredHttpRequest() = runTest {
        historyRepository.insertHistory(httpHistory())

        val inserted = historyRepository.getAllHistories().first() as HistoryItem.Http

        val updated = inserted.copy(
            request = inserted.request.copy(requestUrl = "https://updated.dev")
        )

        historyRepository.updateHistory(updated)

        val result = historyRepository.getAllHistories().first() as HistoryItem.Http

        assertEquals("https://updated.dev", result.request.requestUrl)
    }

    @Test
    fun getAllHistories_returnsAllInsertedHistories() = runTest {
        historyRepository.insertHistory(httpHistory(id = 1))
        historyRepository.insertHistory(webSocketHistory(id = 2))
        val result = historyRepository.getAllHistories()

        assertEquals(2, result.size)
    }

    @Test
    fun getHistory_returnsCorrectWebSocketById() = runTest {
        val item = webSocketHistory(id = 20)
        historyRepository.insertHistory(item)
        val inserted = historyRepository.getAllHistories().first() as HistoryItem.WebSocket
        val result = historyRepository.getHistory(inserted.id) as HistoryItem.WebSocket
        assertEquals(item.request.url, result.request.url)
    }

    @Test
    fun deleteHistory_removesSingleHistoryById() = runTest {
        historyRepository.insertHistory(httpHistory())

        val inserted = historyRepository.getAllHistories().first()
        historyRepository.deleteHistory(inserted.id)

        val result = historyRepository.getAllHistories().size
        assertEquals(0, result)
    }

    @Test
    fun deleteHistories_removesMultipleHistoriesById() = runTest {
        historyRepository.insertHistory(httpHistory())
        historyRepository.insertHistory(webSocketHistory())
        historyRepository.insertHistory(httpHistory())

        val ids = historyRepository.getAllHistories().map { it.id }

        historyRepository.deleteHistories(ids)

        val result = historyRepository.getAllHistories()
        assertEquals(0, result.size)
    }

    @Test
    fun getHistory_returnsCorrectHttpById() = runTest {
        val historyItem = HistoryItem.Http(
            id = 0,
            HttpRequest(requestUrl = "test 11")
        )

        historyRepository.insertHistory(historyItem)

        val inserted = historyRepository.getAllHistories().first()
        val result = historyRepository.getHistory(inserted.id)

        assertEquals(inserted, result)
    }

    @Test
    fun getHistory_returnsAllInsertedHttpAndWebSocket() = runTest {
        val http = httpHistory()
        val webSocket = webSocketHistory()

        historyRepository.insertHistory(http)
        historyRepository.insertHistory(webSocket)

        val items = historyRepository.getAllHistories()

        val httpInserted = items.first { it is HistoryItem.Http }
        val wsInserted = items.first { it is HistoryItem.WebSocket }

        val httpResult = historyRepository.getHistory(httpInserted.id)
        val webSocketResult = historyRepository.getHistory(wsInserted.id)

        assertTrue(httpResult is HistoryItem.Http)
        assertTrue(webSocketResult is HistoryItem.WebSocket)

        assertEquals(httpInserted, httpResult)
        assertEquals(wsInserted, webSocketResult)
    }
}