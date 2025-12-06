package com.example.nextrequest

import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.collection.presentation.model.CollectionUiState
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.domain.searchCollections
import com.example.nextrequest.history.domain.searchHistories
import com.example.nextrequest.history.presentation.model.HistoryEntry
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.Test

class SearchEntriesTest {
    @Test
    fun `return histories by searching url`() {
        val history = listOf(
            History(requestUrl = "url1"),
            History(requestUrl = "url2"),
            History(requestUrl = "url3"),
            History(requestUrl = "url3"),
            History(requestUrl = "url5"),
        )
        val historyRequests: List<HistoryEntry> = listOf(
            HistoryEntry("12 Aug", history),
            HistoryEntry(
                "14 Aug", history + History(requestUrl = "request55")
            )
        )

        val result = searchHistories(historyRequests, "5")
        result shouldBe listOf(
            HistoryEntry("12 Aug", listOf(History(requestUrl = "url5"))),
            HistoryEntry(
                "14 Aug",
                listOf(History(requestUrl = "url5"), History(requestUrl = "request55"))
            )
        )
    }

    @Test
    fun `searchHistories returns emptyList when search query doesn't match anything`() {
        val history = listOf(
            History(requestUrl = "url1"),
            History(requestUrl = "url2")
        )
        val historyRequests = listOf(
            HistoryEntry("12 Aug", history),
            HistoryEntry(
                "14 Aug", history + History(requestUrl = "url3")
            )
        )

        val result = searchHistories(historyRequests, "4")
        result.shouldBeEmpty()
    }

    @Test
    fun `searchHistories performs case-insensitive matching`() {
        val histories = listOf(
            HistoryEntry(
                "12 Aug", histories = listOf(
                    History(requestUrl = "url1"),
                    History(requestUrl = "url2")
                )
            )
        )

        val actual = searchHistories(histories, "URL")
        actual shouldBe histories
    }

    @Test
    fun `searchCollections returns all collections when query is empty`() {
        val c1 = CollectionUiState(
            Collection(
                collectionName = "admin",
                requests = listOf(
                    Request(requestName = "create admin"),
                    Request(requestName = "update admin")
                )
            )
        )
        val c2 = CollectionUiState(
            Collection(
                collectionName = "auth",
                requests = listOf(
                    Request(requestName = "login"),
                    Request(requestName = "register"),
                    Request(requestName = "logout")
                )
            )
        )
        val collections = listOf<CollectionUiState>(c1, c2)
        val actual1 = searchCollections(collections, "")
        actual1 shouldBe collections
        val actual2 = searchCollections(collections, "  ")
        actual2 shouldBe collections
    }

    @Test
    fun `searchCollections returns matching collections by request name or collection name`() {
        val c1 = CollectionUiState(
            Collection(
                collectionName = "admin",
                requests = listOf(
                    Request(requestName = "create admin"),
                    Request(requestName = "update admin")
                )
            )
        )
        val c2 = CollectionUiState(
            Collection(
                collectionName = "auth",
                requests = listOf(
                    Request(requestName = "login"),
                    Request(requestName = "register"),
                    Request(requestName = "logout")
                )
            )
        )
        val c3 = CollectionUiState(
            Collection(
                collectionName = "user",
                requests = listOf(
                    Request(requestName = "get user profile"),
                    Request(requestName = "update user profile")
                )
            )
        )
        val c4 = CollectionUiState(
            Collection(
                collectionName = "product",
                requests = listOf(
                    Request(requestName = "create order"),
                    Request(requestName = "update  order"),
                    Request(requestName = "list user orders")
                )
            )
        )
        val collections = listOf<CollectionUiState>(c1, c2, c3, c4)
        val actual1 = searchCollections(collections, "user")
        val expected1 =
            listOf(
                c3,
                c4.copy(
                    collection = c4.collection.copy(
                        requests = listOf(Request(requestName = "list user orders"))
                    )
                )
            )
        actual1 shouldBe expected1
        val actual2 = searchCollections(collections, "admin")
        val expected2 = listOf(c1)
        actual2 shouldBe expected2

        val actual3 = searchCollections(collections, "login")
        val expected3 =
            listOf(CollectionUiState(c2.collection.copy(requests = listOf(Request(requestName = "login")))))
        actual3 shouldBe expected3
    }

    @Test
    fun `searchCollections performs case-insensitive matching`() {
        val c1 = CollectionUiState(
            Collection(
                collectionName = "user",
                requests = listOf(
                    Request(requestName = "get user profile"),
                    Request(requestName = "update user profile")
                )
            )
        )
        val collections = listOf<CollectionUiState>(c1, CollectionUiState(Collection()))

        val actual1 = searchCollections(collections, "UseR")
        val expected1 = listOf(c1)
        actual1 shouldBe expected1

        val actual2 = searchCollections(collections, "PROFILE")
        actual2 shouldBe expected1

    }
}