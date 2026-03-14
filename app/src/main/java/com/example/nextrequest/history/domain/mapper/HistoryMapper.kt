package com.example.nextrequest.history.domain.mapper

import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.data.extensions.toImageBitmap
import com.example.nextrequest.core.data.extensions.toLocalDate
import com.example.nextrequest.history.data.entity.HistoryEntity
import com.example.nextrequest.history.data.model.HttpRequest
import com.example.nextrequest.history.data.model.WebSocketRequest
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.model.RequestType
import com.google.gson.Gson

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
            id = id,
            type = RequestType.Http,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )

        is HistoryItem.WebSocket -> HistoryEntity(
            id = id,
            type = RequestType.WebSocket,
            data = gson.toJson(this.request),
            createdAt = this.request.createdAt
        )
    }
}

fun HistoryItem.toRequest(): Request {
    return when (this) {
        is HistoryItem.Http -> Request(
            requestName = "${this.request.httpMethod} ${this.request.requestUrl}",
            requestUrl = this.request.requestUrl,
            httpMethod = this.request.httpMethod,
            response = this.request.response,
            imageResponse = this.request.imageResponse?.toImageBitmap(),
            createdAt = this.request.createdAt.toLocalDate(),
            statusCode = this.request.statusCode,
            body = this.request.body,
            headers = this.request.headers
        )

        is HistoryItem.WebSocket -> Request()
    }
}

