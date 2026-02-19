package com.example.nextrequest.history.presentation

import com.example.nextrequest.collection.presentation.model.CollectionEntry
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import com.example.nextrequest.history.presentation.model.HistoryEntry

data class HistoryUiModel(
    val historyEntries: List<HistoryEntry> = emptyList(),
    val expandedStates: List<ExpandableHistoryItem> = emptyList(),
    val collectionNames: Set<CollectionEntry> = emptySet(),
)