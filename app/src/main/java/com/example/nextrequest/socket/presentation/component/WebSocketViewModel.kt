package com.example.nextrequest.socket.presentation.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import com.example.nextrequest.socket.presentation.component.mapper.toUi
import com.example.nextrequest.socket.presentation.component.model.WebSocketUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(
    private val repository: WebSocketRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<UiState<WebSocketUiModel>>(
            UiState.Success(WebSocketUiModel(false, emptyList()))
        )
    val uiState: StateFlow<UiState<WebSocketUiModel>> = _uiState.asStateFlow()

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
                    _uiState.value =
                        UiState.Success(current.copy(isConnected = connected))
                }
        }
    }

    fun connect(url: String) {
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

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}