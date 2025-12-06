package com.example.nextrequest.history.domain

import com.example.nextrequest.collection.presentation.model.CollectionUiState
import com.example.nextrequest.history.presentation.model.HistoryEntry

fun searchHistories(
    entries: List<HistoryEntry>,
    searchQuery: String,
): List<HistoryEntry> {
    if (searchQuery.isBlank()) return entries

    return entries.mapNotNull { entry ->
        val filteredHistories = entry.histories.filter {
            it.requestUrl.contains(searchQuery, ignoreCase = true)
        }
        if (filteredHistories.isNotEmpty()) {
            entry.copy(histories = filteredHistories)
        } else {
            null
        }
    }
}

fun searchCollections(
    items: List<CollectionUiState>,
    searchQuery: String,
): List<CollectionUiState> {
    if (searchQuery.isBlank()) return items
    val result = mutableListOf<CollectionUiState>()
    items.forEach { collection ->
        val isQueryInCollectionName =
            collection.collection.collectionName.contains(searchQuery, ignoreCase = true)
        if (isQueryInCollectionName) {
            result.add(collection)
        } else {
            val requests = collection.collection.requests?.filter {
                it.requestName.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
            if (requests?.isNotEmpty() == true) {
                result.add(collection.copy(collection = collection.collection.copy(requests = requests)))
            }
        }

    }
    return result
}