package com.example.nextrequest.collection.domain.model

import java.util.UUID

data class RequestCollection(
    val collectionId: String = UUID.randomUUID().toString(),
    val collectionName: String = "New Collection",
    val items: List<CollectionItem>? = null,
)