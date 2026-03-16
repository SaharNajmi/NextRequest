package com.example.nextrequest.history.domain.model

import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.domain.model.WebSocketRequest

sealed class HistoryItem {
    abstract val id: Int

    data class Http(override val id: Int, val request: HttpRequest) : HistoryItem()
    data class WebSocket(override val id: Int, val request: WebSocketRequest) : HistoryItem()
}

