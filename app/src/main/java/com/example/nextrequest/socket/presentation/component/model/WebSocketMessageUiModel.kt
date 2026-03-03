package com.example.nextrequest.socket.presentation.component.model

data class WebSocketMessageUiModel(
    val text: String,
    val isSentByUser: Boolean,
    val timestamp: String
)