package com.example.nextrequest.socket.domain.repository

import kotlinx.coroutines.flow.Flow

interface WebSocketRepository {
    val messages: Flow<WebSocketMessage>
    val isConnected: Flow<Boolean>
    fun connect(url: String)
    fun disconnect()
    fun sendMessage(message: WebSocketMessage)
    fun close()
}