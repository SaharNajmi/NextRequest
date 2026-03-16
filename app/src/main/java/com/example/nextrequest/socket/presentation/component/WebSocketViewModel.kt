package com.example.nextrequest.socket.presentation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.core.domain.model.WebSocketRequest
import com.example.nextrequest.core.presentation.navigation.Screens
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.history.presentation.HistoryViewModel
import com.example.nextrequest.home.data.mapper.toHttpRequest
import com.example.nextrequest.home.data.mapper.toHttpResponse
import com.example.nextrequest.home.presentation.HomeUiState
import com.example.nextrequest.home.presentation.Loadable
import com.example.nextrequest.socket.domain.repository.WebSocketMessage
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import com.example.nextrequest.socket.presentation.component.mapper.toUi
import com.example.nextrequest.socket.presentation.component.mapper.toWebSocket
import com.example.nextrequest.socket.presentation.component.model.WebSocketUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val repository: WebSocketRepository,
    private val historyRepository: HistoryRepository,
    private val collectionRepository: CollectionRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<UiState<WebSocketUiModel>>(
            UiState.Success(WebSocketUiModel(false, "", emptyList()))
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
                        ?: WebSocketUiModel(isConnected = false, url = "", messages = emptyList())
                    _uiState.value =
                        UiState.Success(current.copy(messages = listOf(message.toUi()) + current.messages))
                }
        }
        viewModelScope.launch {
            repository.isConnected
                .collect { connected ->
                    val current = (_uiState.value as? UiState.Success)?.data
                        ?: WebSocketUiModel(isConnected = false, url = "", messages = emptyList())

                    if (current.isConnected && !connected) {
                        saveWebSocketHistory()
                    }
                    _uiState.value =
                        UiState.Success(current.copy(isConnected = connected,
                            url = currentUrl ?: current.url))
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

    fun loadRequest(requestId: Int, source: String) {
        viewModelScope.launch(dispatcher) {
            val wsRequest = when (source) {
                Screens.ROUTE_HISTORY_SCREEN -> {
                    when (val saved = historyRepository.getHistory(requestId)) {
                        is HistoryItem.WebSocket -> saved.request
                        is HistoryItem.Http -> null
                    }
                }
                Screens.ROUTE_COLLECTION_SCREEN -> {
                    when (val saved = collectionRepository.getCollectionItem(requestId)) {
                        is CollectionItem.WebSocket -> saved.request
                        is CollectionItem.Http -> null
                    }
                }
                else -> null
            }

            wsRequest?.let {
                _uiState.value = UiState.Success(
                    WebSocketUiModel(
                        isConnected = false,
                        url = it.url,
                        messages = it.messages.map { msg -> msg.toUi() }
                    )
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}