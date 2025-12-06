package com.example.nextrequest.history.presentation.model

import com.example.nextrequest.history.domain.model.History

data class HistoryEntry(
    val dateCreated: String,
    val histories: List<History>
)