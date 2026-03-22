package com.example.nextrequest.socket.data.network

import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WebSocketRepositoryImp @Inject constructor(
    val client: OkHttpClient,
    private val scope: CoroutineScope,
) : WebSocketRepository {

    private var webSocket: WebSocket? = null
    private val _messages = MutableSharedFlow<WebSocketMessage>(replay = 1)
    override val messages: Flow<WebSocketMessage> = _messages.asSharedFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

    override fun connect(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch {
                    _messages.emit(WebSocketMessage("Connected to $url", false))
                    _isConnected.value = true
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    _messages.emit(WebSocketMessage(text, false))
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch {
                    _messages.emit(WebSocketMessage(t.message ?: "Connection failed: ${t.javaClass.simpleName}", false))
                    _isConnected.value = false
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch {
                    _messages.emit(WebSocketMessage("Disconnected from $url $reason", false))
                    _isConnected.value = false
                }
            }
        })
    }

    override fun sendMessage(message: String) {
        scope.launch {
            webSocket?.send(message)
            _messages.emit(WebSocketMessage(message, true))
        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "User disconnected")
    }

    override fun close() {
        webSocket?.close(1000, "connection closed")
        scope.cancel()
    }
}