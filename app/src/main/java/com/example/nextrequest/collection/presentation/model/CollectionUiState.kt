package com.example.nextrequest.collection.presentation.model

import com.example.nextrequest.collection.domain.model.RequestCollection

data class CollectionUiState(
    val requestCollection: RequestCollection,
    val isExpanded: Boolean = false
)