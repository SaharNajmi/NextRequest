package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.presentation.model.CollectionUiState
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.searchCollections
import com.example.nextrequest.history.domain.searchHistories
import com.example.nextrequest.history.presentation.model.HistoryEntry
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SearchEntriesTest {

    private val fixedTime = 1000000000000L

    private fun httpItem(requestName: String) = CollectionItem.Http(
        requestId = 0,
        requestName = requestName,
        request = HttpRequest(requestUrl = "", createdAt = fixedTime)
    )

    @Test
    fun `return histories by searching url for http and websocket`() {
        val httpItems = listOf(
            HistoryItem.Http(id = 1, HttpRequest(requestUrl = "url1")),
            HistoryItem.Http(id = 2, HttpRequest(requestUrl = "url2")),
            HistoryItem.Http(id = 3, HttpRequest(requestUrl = "url3")),
            HistoryItem.Http(id = 4, HttpRequest(requestUrl = "url3")),
            HistoryItem.Http(id = 5, HttpRequest(requestUrl = "url5")),
            HistoryItem.Http(id = 6, HttpRequest(requestUrl = "request55"))
        )

        val wsItems = listOf(
            HistoryItem.WebSocket(
                id = 7, WebSocketRequest(
                    url = "ws://url5",
                    createdAt = System.currentTimeMillis()
                )
            ),
            HistoryItem.WebSocket(
                id = 8, WebSocketRequest(
                    url = "ws://request55",
                    createdAt = System.currentTimeMillis()
                )
            )
        )

        val histories: List<HistoryEntry> = listOf(
            HistoryEntry("12 Aug", httpItems.take(5) + wsItems.take(1)),
            HistoryEntry("14 Aug", httpItems + wsItems)
        )

        val result = searchHistories(histories, "5")

        val expected = listOf(
            HistoryEntry(
                "12 Aug",
                listOf(
                    httpItems[4],   // url5
                    wsItems[0]      // ws://url5
                )
            ),
            HistoryEntry(
                "14 Aug",
                listOf(
                    httpItems[4],   // url5
                    httpItems[5],   // request55
                    wsItems[0],     // ws://url5
                    wsItems[1]      // ws://request55
                )
            )
        )

        result shouldBe expected
    }

    @Test
    fun `search returns empty list when no matches`() {
        val histories = listOf(
            HistoryEntry(
                "12 Aug",
                listOf(
                    HistoryItem.Http(id = 1, HttpRequest(requestUrl = "url1")),
                    HistoryItem.WebSocket(id = 2, WebSocketRequest(url = "ws://url2"))
                )
            )
        )

        val result = searchHistories(histories, "url22")

        result shouldBe emptyList()
    }

    @Test
    fun `searchHistories performs case-insensitive matching`() {
        val histories = listOf(
            HistoryEntry(
                "12 Aug", histories = listOf(
                    HistoryItem.Http(id = 1, HttpRequest(requestUrl = "url1")),
                    HistoryItem.Http(id = 2, HttpRequest(requestUrl = "url2")),
                    HistoryItem.WebSocket(
                        id = 3, WebSocketRequest(
                            url = "ws://Url3",
                            createdAt = System.currentTimeMillis()
                        )
                    )
                )
            )
        )
        val actual = searchHistories(histories, "URL")
        actual shouldBe histories
    }

    @Test
    fun `searchCollections returns all collections when query is empty`() {
        val c1 = CollectionUiState(
            RequestCollection(
                collectionName = "admin",
                items = listOf(httpItem("create admin"), httpItem("update admin"))
            )
        )
        val c2 = CollectionUiState(
            RequestCollection(
                collectionName = "auth",
                items = listOf(httpItem("login"), httpItem("register"), httpItem("logout"))
            )
        )
        val collections = listOf(c1, c2)

        searchCollections(collections, "") shouldBe collections
        searchCollections(collections, "  ") shouldBe collections
    }

    @Test
    fun `searchCollections returns matching collections by request name or collection name`() {
        val c1 = CollectionUiState(
            RequestCollection(
                collectionName = "admin",
                items = listOf(httpItem("create admin"), httpItem("update admin"))
            )
        )
        val c2 = CollectionUiState(
            RequestCollection(
                collectionName = "auth",
                items = listOf(httpItem("login"), httpItem("register"), httpItem("logout"))
            )
        )
        val c3 = CollectionUiState(
            RequestCollection(
                collectionName = "user",
                items = listOf(httpItem("get user profile"), httpItem("update user profile"))
            )
        )
        val c4 = CollectionUiState(
            RequestCollection(
                collectionName = "product",
                items = listOf(httpItem("create order"), httpItem("update order"), httpItem("list user orders"))
            )
        )

        val collections = listOf(c1, c2, c3, c4)

        val actual1 = searchCollections(collections, "user")
        val expected1 = listOf(
            c3,
            c4.copy(
                requestCollection = c4.requestCollection.copy(
                    items = listOf(httpItem("list user orders"))
                )
            )
        )
        actual1 shouldBe expected1

        val actual2 = searchCollections(collections, "admin")
        actual2 shouldBe listOf(c1)

        val actual3 = searchCollections(collections, "login")
        val expected3 = listOf(
            CollectionUiState(
                c2.requestCollection.copy(items = listOf(httpItem("login")))
            )
        )
        actual3 shouldBe expected3
    }

    @Test
    fun `searchCollections performs case-insensitive matching`() {
        val c1 = CollectionUiState(
            RequestCollection(
                collectionName = "user",
                items = listOf(
                    httpItem("get user profile"),
                    httpItem("update user profile")
                )
            )
        )
        val collections = listOf(c1, CollectionUiState(RequestCollection()))

        searchCollections(collections, "UseR") shouldBe listOf(c1)
        searchCollections(collections, "PROFILE") shouldBe listOf(c1)
    }
}