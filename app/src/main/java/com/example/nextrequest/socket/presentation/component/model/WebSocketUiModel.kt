package com.example.nextrequest.socket.presentation.component.model

data class WebSocketUiModel(
    val isConnected: Boolean,
    val messages: List<MessageUiModel>,
)