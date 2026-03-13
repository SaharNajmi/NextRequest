package com.example.nextrequest.history.presentation

import com.example.nextrequest.history.domain.model.HistoryItem

data class HistoryCallbacks(
    val onHistoryItemClick: (Int) -> Unit,
    val onAddHistoryToCollection: (HistoryItem, String) -> Unit,
    val onAddHistoriesToCollection: (List<HistoryItem>, String) -> Unit,
    val onHeaderClick: (String) -> Unit,
    val onDeleteHistoriesClick: (historyIds: List<HistoryItem>) -> Unit,
    val onDeleteHistoryClick: (HistoryItem) -> Unit,
    val onCreateNewCollectionClick: () -> Unit,
)