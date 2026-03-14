package com.example.nextrequest.socket.presentation.component.model

data class WebSocketUiModel(
    val isConnected: Boolean,
    val url: String,
    val messages: List<MessageUiModel>,
)