package com.example.nextrequest.history.domain.mapper

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.core.data.extensions.toLocalDate
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.history.data.entity.HistoryEntity
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.model.RequestType
import com.google.gson.Gson
import java.time.LocalDate

fun HistoryEntity.toDomain(): HistoryItem {
    val gson = Gson()
    return when (type) {
        RequestType.Http -> HistoryItem.Http(
            id = id, request = gson.fromJson(data, HttpRequest::class.java)
        )

        RequestType.WebSocket -> HistoryItem.WebSocket(
            id = id, request = gson.fromJson(data, WebSocketRequest::class.java)
        )
    }
}

fun HistoryItem.toEntity(): HistoryEntity {
    val gson = Gson()
    return when (this) {
        is HistoryItem.Http -> HistoryEntity(
            type = RequestType.Http,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )

        is HistoryItem.WebSocket -> HistoryEntity(
            type = RequestType.WebSocket,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )
    }
}

fun HistoryItem.toCollectionItem(): CollectionItem {
        return when (this) {
            is HistoryItem.Http -> CollectionItem.Http(
                requestId = 0,
                requestName ="${this.request.httpMethod} ${this.request.requestUrl}",
                request = this.request
            )
            is HistoryItem.WebSocket -> CollectionItem.WebSocket(
                requestId = 0,
                requestName ="WebSocket ${this.request.url}",
                request = this.request
            )
        }
}

fun HistoryItem.getCreatedAt(): LocalDate = when (this) {
    is HistoryItem.Http -> request.createdAt.toLocalDate()
    is HistoryItem.WebSocket -> request.createdAt.toLocalDate()
}