package com.example.nextrequest.socket.presentation.component.model

data class WebSocketUiModel(
    val isConnected: Boolean = false,
    val url: String = "",
    val visibleMessages: List<MessageUiModel> = emptyList(),
    val hiddenMessages: List<MessageUiModel> = emptyList(),
)