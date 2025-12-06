package com.example.nextrequest.collection.presentation.model

import com.example.nextrequest.collection.domain.model.Collection

data class CollectionUiState(
    val collection: Collection,
    val isExpanded: Boolean = false
)