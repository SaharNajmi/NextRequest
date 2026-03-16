package com.example.nextrequest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nextrequest.collection.data.repository.CollectionRepositoryImp
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionRepositoryImpTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var collectionRepository: CollectionRepository
    private lateinit var db: AppDatabase
    private val collectionId = "collection-123"

    private val httpItem1 = CollectionItem.Http(
        requestId = 0,
        requestName = "GET Users",
        request = HttpRequest(
            requestUrl = "https://api.example.com/users",
            httpMethod = HttpMethod.GET
        )
    )
    private val httpItem2 = CollectionItem.Http(
        requestId = 0,
        requestName = "POST Login",
        request = HttpRequest(
            requestUrl = "https://api.example.com/login",
            httpMethod = HttpMethod.POST,
            body = """{"user":"test"}"""
        )
    )
    private val wsItem1 = CollectionItem.WebSocket(
        requestId = 0,
        requestName = "Chat Socket",
        request = WebSocketRequest(url = "wss://chat.example.com/ws")
    )
    private val wsItem2 = CollectionItem.WebSocket(
        requestId = 0,
        requestName = "Notifications Socket",
        request = WebSocketRequest(
            url = "wss://notify.example.com/ws"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        collectionRepository = CollectionRepositoryImp(db.collectionDao(), dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        db.close()
    }

    private suspend fun createCollection(id: String = collectionId, vararg items: CollectionItem) {
        collectionRepository.insertCollection(RequestCollection(collectionId = id))
        items.forEach { collectionRepository.insertItemToCollection(id, it) }
    }

    private suspend fun getSavedItem(name: String): CollectionItem {
        return collectionRepository.getAllCollections()
            .flatMap { it.items ?: emptyList() }
            .first { it.requestName == name }
    }

    @Test
    fun insertCollection_savedCollectionCanBeFound() = runTest {
        collectionRepository.insertCollection(
            RequestCollection(collectionId = "new-id", collectionName = "My API")
        )
        val all = collectionRepository.getAllCollections()
        assertEquals(1, all.size)
        assertEquals("new-id", all[0].collectionId)
        assertEquals("My API", all[0].collectionName)
    }

    @Test
    fun getAllCollections_twoCollectionsSaved_bothAreReturned() = runTest {
        createCollection("col-A", httpItem1)
        createCollection("col-B", wsItem1)
        assertEquals(2, collectionRepository.getAllCollections().size)
    }

    @Test
    fun getAllCollections_collectionWithHttpItem_httpFieldsAreCorrect() = runTest {
        createCollection(collectionId, httpItem2)
        val result = collectionRepository.getAllCollections()
            .first().items?.first() as CollectionItem.Http
        assertEquals(httpItem2.requestName, result.requestName)
        assertEquals(httpItem2.request.requestUrl, result.request.requestUrl)
        assertEquals(httpItem2.request.httpMethod, result.request.httpMethod)
        assertEquals(httpItem2.request.body, result.request.body)
    }

    @Test
    fun getAllCollections_collectionWithWebSocketItem_webSocketFieldsAreCorrect() = runTest {
        createCollection(collectionId, wsItem2)
        val result = collectionRepository.getAllCollections()
            .first().items?.first() as CollectionItem.WebSocket
        assertEquals(wsItem2.requestName, result.requestName)
        assertEquals(wsItem2.request.url, result.request.url)
        assertTrue(result.request.messages.isEmpty())
    }

    @Test
    fun getAllCollections_twoCollectionsWithDifferentItems_eachCollectionHasItsOwnItems() =
        runTest {
            createCollection("col-A", httpItem1, httpItem2)
            createCollection("col-B", wsItem1)
            val all = collectionRepository.getAllCollections()
            val colA = all.first { it.collectionId == "col-A" }
            val colB = all.first { it.collectionId == "col-B" }
            assertEquals(2, colA.items?.size)
            assertEquals(1, colB.items?.size)
            assertTrue(colB.items?.first() is CollectionItem.WebSocket)
        }

    @Test
    fun updateCollection_collectionNameChanged_newNameIsSaved() = runTest {
        val collection = RequestCollection(collectionId = collectionId, collectionName = "Old Name")
        collectionRepository.insertCollection(collection)
        collectionRepository.updateCollection(collection.copy(collectionName = "New Name"))
        assertEquals("New Name", collectionRepository.getAllCollections().first().collectionName)
    }

    @Test
    fun deleteCollection_collectionWithHttpItems_allItemsAreRemoved() = runTest {
        createCollection(collectionId, httpItem1, httpItem2)
        collectionRepository.deleteCollection(collectionId)
        assertEquals(0, collectionRepository.getAllCollections().size)
    }

    @Test
    fun deleteCollection_collectionWithWebSocketItems_allItemsAreRemoved() = runTest {
        createCollection(collectionId, wsItem1, wsItem2)
        collectionRepository.deleteCollection(collectionId)
        assertEquals(0, collectionRepository.getAllCollections().size)
    }

    @Test
    fun deleteCollection_oneOfTwoCollectionsDeleted_otherCollectionStillExists() = runTest {
        createCollection("col-A", httpItem1, wsItem1)
        createCollection("col-B", httpItem2)
        collectionRepository.deleteCollection("col-A")
        val remaining = collectionRepository.getAllCollections()
        assertEquals(1, remaining.size)
        assertEquals("col-B", remaining.first().collectionId)
    }

    @Test
    fun insertItemToCollection_httpItemInserted_canBeFoundById() = runTest {
        createCollection(collectionId, httpItem1)
        val saved = getSavedItem(httpItem1.requestName)
        val result = collectionRepository.getCollectionItem(saved.requestId)
        assertTrue(result is CollectionItem.Http)
    }

    @Test
    fun insertItemToCollection_webSocketItemInserted_canBeFoundById() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName)
        val result = collectionRepository.getCollectionItem(saved.requestId)
        assertTrue(result is CollectionItem.WebSocket)
    }

    @Test
    fun insertItemToCollection_httpAndWebSocketInserted_bothCanBeFoundById() = runTest {
        createCollection(collectionId, httpItem1, wsItem1)
        val savedHttp = getSavedItem(httpItem1.requestName)
        val savedWs = getSavedItem(wsItem1.requestName)
        assertTrue(collectionRepository.getCollectionItem(savedHttp.requestId) is CollectionItem.Http)
        assertTrue(collectionRepository.getCollectionItem(savedWs.requestId) is CollectionItem.WebSocket)
    }

    @Test
    fun getCollectionItems_collectionHasHttpItem_allHttpFieldsAreCorrect() = runTest {
        createCollection(collectionId, httpItem2)
        val result =
            collectionRepository.getCollectionItems(collectionId).first() as CollectionItem.Http
        assertEquals(httpItem2.requestName, result.requestName)
        assertEquals(httpItem2.request.requestUrl, result.request.requestUrl)
        assertEquals(httpItem2.request.httpMethod, result.request.httpMethod)
        assertEquals(httpItem2.request.body, result.request.body)
    }

    @Test
    fun getCollectionItems_collectionHasWebSocketItem_allWebSocketFieldsAreCorrect() = runTest {
        createCollection(collectionId, wsItem2)
        val result = collectionRepository.getCollectionItems(collectionId).first() as CollectionItem.WebSocket
        assertEquals(wsItem2.requestName, result.requestName)
        assertEquals(wsItem2.request.url, result.request.url)
        assertTrue(result.request.messages.isEmpty())
    }
    @Test
    fun getCollectionItems_twoCollectionsExist_onlyRequestedCollectionItemsAreReturned() = runTest {
        createCollection("col-A", httpItem1, httpItem2)
        createCollection("col-B", wsItem1)
        assertEquals(2, collectionRepository.getCollectionItems("col-A").size)
        assertEquals(1, collectionRepository.getCollectionItems("col-B").size)
    }

    @Test
    fun getCollectionItem_httpItemExists_correctDataIsReturned() = runTest {
        createCollection(collectionId, httpItem1)
        val saved = getSavedItem(httpItem1.requestName)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.Http
        assertEquals(httpItem1.requestName, result.requestName)
        assertEquals(httpItem1.request.requestUrl, result.request.requestUrl)
        assertEquals(httpItem1.request.httpMethod, result.request.httpMethod)
    }

    @Test
    fun getCollectionItem_webSocketItemExists_correctDataIsReturned() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.WebSocket
        assertEquals(wsItem1.requestName, result.requestName)
        assertEquals(wsItem1.request.url, result.request.url)
    }

    @Test
    fun getCollectionItem_webSocketItemHasMessages_allMessagesAreCorrect() = runTest {
        val wsItemWithMessages = CollectionItem.WebSocket(
            requestId = 0,
            requestName = "Socket With Messages",
            request = WebSocketRequest(
                url = "wss://example.com/ws",
                messages = listOf(
                    WebSocketMessage(text = "hi", isSentByUser = true),
                    WebSocketMessage(text = "hello", isSentByUser = false)
                )
            )
        )
        createCollection(collectionId, wsItemWithMessages)
        val saved = getSavedItem(wsItemWithMessages.requestName)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.WebSocket
        assertEquals(2, result.request.messages.size)
        assertEquals("hi", result.request.messages[0].text)
        assertTrue(result.request.messages[0].isSentByUser)
        assertEquals("hello", result.request.messages[1].text)
        assertFalse(result.request.messages[1].isSentByUser)
    }

    @Test
    fun updateCollectionItem_httpItemUrlChanged_newUrlIsSaved() = runTest {
        createCollection(collectionId, httpItem1)
        val saved = getSavedItem(httpItem1.requestName) as CollectionItem.Http
        val updated =
            saved.copy(request = saved.request.copy(requestUrl = "https://api.example.com/v2/users"))
        collectionRepository.updateCollectionItem(collectionId, updated)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.Http
        assertEquals("https://api.example.com/v2/users", result.request.requestUrl)
    }

    @Test
    fun updateCollectionItem_httpItemMethodChanged_newMethodIsSaved() = runTest {
        createCollection(
            collectionId,
            httpItem1.copy(request = httpItem1.request.copy(httpMethod = HttpMethod.DELETE))
        )
        val saved = getSavedItem(httpItem1.requestName) as CollectionItem.Http
        val updated = saved.copy(request = saved.request.copy(httpMethod = HttpMethod.DELETE))
        collectionRepository.updateCollectionItem(collectionId, updated)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.Http
        assertEquals(HttpMethod.DELETE, result.request.httpMethod)
    }

    @Test
    fun updateCollectionItem_webSocketUrlChanged_newUrlIsSaved() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName) as CollectionItem.WebSocket
        val updated = saved.copy(request = saved.request.copy(url = "wss://chat.example.com/v2/ws"))
        collectionRepository.updateCollectionItem(collectionId, updated)

        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.WebSocket
        assertEquals("wss://chat.example.com/v2/ws", result.request.url)
    }
    @Test
    fun updateCollectionItem_webSocketMessagesChanged_newMessagesAreSaved() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName) as CollectionItem.WebSocket
        val updated = saved.copy(
            request = saved.request.copy(
                messages = listOf(WebSocketMessage(text = "hello", isSentByUser = true))
            )
        )
        collectionRepository.updateCollectionItem(collectionId, updated)
        val result = collectionRepository.getCollectionItem(saved.requestId) as CollectionItem.WebSocket
        assertEquals(1, result.request.messages.size)
        assertEquals("hello", result.request.messages[0].text)
    }

    @Test
    fun deleteItemFromCollection_oneHttpItemDeleted_otherHttpItemStillExists() = runTest {
        createCollection(collectionId, httpItem1, httpItem2)
        val savedItem1 = getSavedItem(httpItem1.requestName)
        val savedItem2 = getSavedItem(httpItem2.requestName)
        collectionRepository.deleteItemFromCollection(savedItem1.requestId)
        val remaining = collectionRepository.getCollectionItems(collectionId)
        assertEquals(1, remaining.size)
        assertEquals(savedItem2.requestId, remaining.first().requestId)
    }

    @Test
    fun deleteItemFromCollection_oneWebSocketItemDeleted_otherWebSocketItemStillExists() = runTest {
        createCollection(collectionId, wsItem1, wsItem2)
        val savedItem1 = getSavedItem(wsItem1.requestName)
        val savedItem2 = getSavedItem(wsItem2.requestName)
        collectionRepository.deleteItemFromCollection(savedItem1.requestId)
        val remaining = collectionRepository.getCollectionItems(collectionId)
        assertEquals(1, remaining.size)
        assertEquals(savedItem2.requestId, remaining.first().requestId)
    }

    @Test
    fun deleteItemFromCollection_httpItemDeleted_webSocketItemIsNotAffected() = runTest {
        createCollection(collectionId, httpItem1, wsItem1)
        val savedHttp = getSavedItem(httpItem1.requestName)
        collectionRepository.deleteItemFromCollection(savedHttp.requestId)
        val remaining = collectionRepository.getCollectionItems(collectionId)
        assertEquals(1, remaining.size)
        assertTrue(remaining.first() is CollectionItem.WebSocket)
    }

    @Test
    fun deleteItemFromCollection_webSocketItemDeleted_httpItemIsNotAffected() = runTest {
        createCollection(collectionId, httpItem1, wsItem1)
        val savedWs = getSavedItem(wsItem1.requestName)
        collectionRepository.deleteItemFromCollection(savedWs.requestId)
        val remaining = collectionRepository.getCollectionItems(collectionId)
        assertEquals(1, remaining.size)
        assertTrue(remaining.first() is CollectionItem.Http)
    }

    @Test
    fun getRequestName_httpItemExists_correctNameIsReturned() = runTest {
        createCollection(collectionId, httpItem1)
        val saved = getSavedItem(httpItem1.requestName)
        assertEquals(httpItem1.requestName, collectionRepository.getRequestName(saved.requestId))
    }

    @Test
    fun getRequestName_webSocketItemExists_correctNameIsReturned() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName)
        assertEquals(wsItem1.requestName, collectionRepository.getRequestName(saved.requestId))
    }

    @Test
    fun changeRequestName_httpItemRenamed_newNameIsSaved() = runTest {
        createCollection(collectionId, httpItem1)
        val saved = getSavedItem(httpItem1.requestName)
        collectionRepository.changeRequestName(saved.requestId, "Renamed HTTP")
        assertEquals("Renamed HTTP", collectionRepository.getRequestName(saved.requestId))
    }

    @Test
    fun changeRequestName_webSocketItemRenamed_newNameIsSaved() = runTest {
        createCollection(collectionId, wsItem1)
        val saved = getSavedItem(wsItem1.requestName)
        collectionRepository.changeRequestName(saved.requestId, "Renamed WS")
        assertEquals("Renamed WS", collectionRepository.getRequestName(saved.requestId))
    }

    @Test
    fun changeRequestName_httpItemRenamed_webSocketItemNameIsNotChanged() = runTest {
        createCollection(collectionId, httpItem1, wsItem1)
        val savedHttp = getSavedItem(httpItem1.requestName)
        val savedWs = getSavedItem(wsItem1.requestName)
        collectionRepository.changeRequestName(savedHttp.requestId, "Renamed")
        assertEquals(wsItem1.requestName, collectionRepository.getRequestName(savedWs.requestId))
    }
}