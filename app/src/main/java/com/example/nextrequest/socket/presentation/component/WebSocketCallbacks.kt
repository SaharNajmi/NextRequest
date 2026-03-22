package com.example.nextrequest.socket.presentation.component

data class WebSocketCallbacks(
    val onConnectClick: () -> Unit,
    val onDisconnectClick: () -> Unit,
    val onSendMessageClick: () -> Unit,
    val onHideMessages: () -> Unit,
    val onShowHiddenMessages: () -> Unit,
)