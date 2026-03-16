package com.example.nextrequest.collection.domain.model

import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest

sealed class CollectionItem {
    abstract val requestId: Int
    abstract val requestName: String

    data class Http(
        override val requestId: Int,
        override val requestName: String,
        val request: HttpRequest,
    ) : CollectionItem()

    data class WebSocket(
        override val requestId: Int,
        override val requestName: String,
        val request: WebSocketRequest,
    ) : CollectionItem()
}