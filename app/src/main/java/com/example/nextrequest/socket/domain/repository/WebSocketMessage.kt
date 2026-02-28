package com.example.nextrequest.socket.domain.repository

data class WebSocketMessage(
    val text: String,
    //val isSentByUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)