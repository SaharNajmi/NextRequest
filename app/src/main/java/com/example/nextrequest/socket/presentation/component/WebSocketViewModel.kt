package com.example.nextrequest.socket.presentation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val repository: WebSocketRepository,
) : ViewModel() {
    var isConnected = repository.isConnected

    private val _messages = MutableStateFlow<List<WebSocketMessage>>(emptyList())
    val messages: StateFlow<List<WebSocketMessage>> = _messages

    init {
        viewModelScope.launch {
            repository.messages.collect { message ->
                _messages.update { it + message }
            }
        }
    }

    fun connect(url: String) {
        repository.connect(url)
    }

    fun disconnect() {
        repository.disconnect()
    }

    fun sendMessage(message: String) {
        repository.sendMessage(WebSocketMessage(message))
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}