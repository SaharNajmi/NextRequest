package com.example.nextrequest.socket.presentation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.history.data.model.WebSocketRequest
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.history.presentation.HistoryViewModel
import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import com.example.nextrequest.socket.presentation.component.mapper.toUi
import com.example.nextrequest.socket.presentation.component.mapper.toWebSocket
import com.example.nextrequest.socket.presentation.component.model.WebSocketUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val repository: WebSocketRepository,
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<UiState<WebSocketUiModel>>(
            UiState.Success(WebSocketUiModel(false, emptyList()))
        )
    val uiState: StateFlow<UiState<WebSocketUiModel>> = _uiState.asStateFlow()
    private var currentUrl: String? = null

    init {
        viewModelScope.launch {
            repository.messages
                .catch { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { message ->
                    val current = (_uiState.value as? UiState.Success)?.data
                        ?: WebSocketUiModel(isConnected = false, messages = emptyList())
                    _uiState.value =
                        UiState.Success(current.copy(messages = listOf(message.toUi()) + current.messages))
                }
        }
        viewModelScope.launch {
            repository.isConnected
                .collect { connected ->
                    val current = (_uiState.value as? UiState.Success)?.data
                        ?: WebSocketUiModel(isConnected = false, messages = emptyList())

                    if (current.isConnected && !connected) {
                        saveWebSocketHistory()
                    }
                    _uiState.value =
                        UiState.Success(current.copy(isConnected = connected))
                }
        }
    }

    fun connect(url: String) {
        currentUrl = url
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.connect(url)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Connection failed")
            }
        }
    }

    fun disconnect() {
        repository.disconnect()
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                repository.sendMessage(message)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Sending message failed")
            }
        }
    }

    private fun saveWebSocketHistory() {
        val url = currentUrl ?: return
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return

        val wsRequest = WebSocketRequest(
            url = url,
            messages = currentState.messages.map { message ->
                message.toWebSocket()
            }
        )

        val historyItem = HistoryItem.WebSocket(
            id = System.currentTimeMillis().toInt(),
            request = wsRequest
        )

        viewModelScope.launch {
            historyRepository.insertHistory(historyItem)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}