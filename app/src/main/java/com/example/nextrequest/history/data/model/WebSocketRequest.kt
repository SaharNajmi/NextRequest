package com.example.nextrequest.history.data.model

import com.example.nextrequest.socket.domain.repository.WebSocketMessage

data class WebSocketRequest(
    val url: String,
    val messages: List<WebSocketMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
)