package com.example.nextrequest.collection.data.mapper

import com.example.nextrequest.collection.data.entity.CollectionEntity
import com.example.nextrequest.collection.data.entity.CollectionItemEntity
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.history.domain.model.RequestType
import com.google.gson.Gson

fun CollectionEntity.toDomain(collectionItems: List<CollectionItem>): RequestCollection {
    return RequestCollection(
        collectionId = collectionId,
        collectionName = collectionName,
        items = collectionItems
    )
}

fun RequestCollection.toEntity(): CollectionEntity {
    return CollectionEntity(
        collectionId = collectionId,
        collectionName = collectionName
    )
}

fun CollectionItem.toEntity(
    collectionId: String,
): CollectionItemEntity {
    val gson = Gson()
    return when (this) {
        is CollectionItem.Http -> CollectionItemEntity(
            id = this.requestId,
            collectionId = collectionId,
            requestName = requestName,
            type = RequestType.Http,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )

        is CollectionItem.WebSocket -> CollectionItemEntity(
            id = this.requestId,
            collectionId = collectionId,
            requestName = requestName,
            type = RequestType.WebSocket,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )
    }
}

fun CollectionItem.toEntity(
    collectionId: String,
    requestName: String,
): CollectionItemEntity {
    val gson = Gson()
    return when (this) {
        is CollectionItem.Http -> CollectionItemEntity(
            id = this.requestId,
            collectionId = collectionId,
            requestName = requestName,
            type = RequestType.Http,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )

        is CollectionItem.WebSocket -> CollectionItemEntity(
            id = this.requestId,
            collectionId = collectionId,
            requestName = requestName,
            type = RequestType.WebSocket,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )
    }
}

fun CollectionItemEntity.toDomain(): CollectionItem {
    val gson = Gson()
    return when (type) {
        RequestType.Http -> CollectionItem.Http(
            requestId = id,
            requestName = requestName,
            request = gson.fromJson(data, HttpRequest::class.java)
        )

        RequestType.WebSocket -> CollectionItem.WebSocket(
            requestId = id,
            requestName = requestName,
            request = gson.fromJson(data, WebSocketRequest::class.java)
        )
    }
}

