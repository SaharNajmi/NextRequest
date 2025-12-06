package com.example.nextrequest.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.core.domain.repository.ApiService
import com.example.nextrequest.core.extensions.buildUrlWithParams
import com.example.nextrequest.core.extensions.mapKeyValuePairsToQueryParameter
import com.example.nextrequest.core.extensions.removeParameterFromUrl
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.home.data.mapper.httpRequestToHistory
import com.example.nextrequest.home.data.mapper.httpRequestToRequest
import com.example.nextrequest.home.data.mapper.toHttpRequest
import com.example.nextrequest.home.data.mapper.toHttpResponse
import com.example.nextrequest.home.presentation.util.getNetworkErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val historyRepository: HistoryRepository,
    private val collectionRepository: CollectionRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(
        HomeUiState(ApiRequest(), Loadable.Empty)
    )

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun sendRequest(collectionId: String? = null) {
        val requestData = uiState.value.data
        if (requestData.requestUrl.isBlank()) return

        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(response = Loadable.Loading)
            try {
                val result = apiService.sendRequest(
                    method = requestData.httpMethod.name,
                    url = requestData.baseUrl,
                    headers = requestData.headers,
                    parameters = requestData.params,
                    body = requestData.body
                )

                _uiState.value = _uiState.value.copy(
                    response = Loadable.Success(result)
                )
                saveToHistory(
                    requestData,
                    result
                )

                if (collectionId != null) {
                    updateCollectionRequest(
                        collectionId = collectionId,
                        httpRequestToRequest(
                            apiRequest = requestData,
                            apiResponse = result
                        )
                    )
                }

            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    response = Loadable.NetworkError(
                        error.getNetworkErrorMessage()
                    )
                )
                saveToHistory(
                    requestData,
                    ApiResponse(error.getNetworkErrorMessage())
                )
                if (collectionId != null) {
                    updateCollectionRequest(
                        collectionId = collectionId,
                        httpRequestToRequest(
                            apiRequest = requestData,
                            apiResponse = ApiResponse(error.getNetworkErrorMessage())
                        )
                    )
                }
            }
        }
    }

    fun clearData() {
        _uiState.value = HomeUiState(ApiRequest(), Loadable.Empty)
    }

    fun updateRequestUrl(newUrl: String) {
        _uiState.value =
            _uiState.value.copy(
                data = _uiState.value.data.copy(
                    requestUrl = newUrl,
                )
            )
    }

    fun updateHttpMethod(newHttpMethod: HttpMethod) {
        _uiState.value = _uiState.value.copy(_uiState.value.data.copy(httpMethod = newHttpMethod))
    }

    fun updateBody(body: String) {
        _uiState.value = _uiState.value.copy(_uiState.value.data.copy(body = body))
    }

    fun addHeader(key: String, value: String) {
        val currentHeaders = _uiState.value.data.headers.orEmpty().toMutableList()

        if (key.isBlank() || value.isBlank())
            return
        val modifiedValue = if (key.equals("Authorization", ignoreCase = true)) {
            currentHeaders.removeIf { it.first.equals("Authorization", ignoreCase = true) }
            if (value.startsWith("Bearer ")) {
                value
            } else "Bearer $value"
        } else {
            value
        }

        currentHeaders.add(key to modifiedValue)

        _uiState.value =
            _uiState.value.copy(data = _uiState.value.data.copy(headers = currentHeaders))
    }

    fun removeHeader(key: String, value: String) {
        val currentHeaders = _uiState.value.data.headers
        val updatedHeaders = currentHeaders?.filterNot { it.first == key && it.second == value }
        _uiState.value =
            _uiState.value.copy(
                data = _uiState.value.data.copy(
                    headers = updatedHeaders,
                )
            )
    }

    fun addParameter(key: String, value: String) {
        if (key.isBlank()) return
        val currentParams = _uiState.value.data.params
        val updatedParams = currentParams.orEmpty() + (key to value)
        updateParamsUiState(updatedParams)
    }

    fun removeParameter(key: String, value: String) {
        val originalUrl = _uiState.value.data.requestUrl
        val newUrl = originalUrl.removeParameterFromUrl(key, value)
        updateRequestUrl(newUrl)
    }

    private fun updateParamsUiState(params: KeyValueList) {
        val url = _uiState.value.data.requestUrl.buildUrlWithParams(
            params.mapKeyValuePairsToQueryParameter()
        )
        _uiState.value =
            _uiState.value.copy(
                data = _uiState.value.data.copy(
                    requestUrl = url,
                )
            )
    }

    fun saveToHistory(
        apiRequest: ApiRequest,
        response: ApiResponse,
    ) {
        viewModelScope.launch(dispatcher) {
            historyRepository.insertHistoryRequest(
                httpRequestToHistory(apiRequest, response)
            )
        }
    }

    fun loadRequestFromHistory(historyId: Int) {
        viewModelScope.launch(dispatcher) {
            val saved = historyRepository.getHistoryRequest(historyId)
            val response = if (saved.statusCode != null)
                Loadable.Success(
                    saved.toHttpResponse()
                )
            else
                Loadable.Error(
                    saved.toHttpResponse().response
                )

            _uiState.value = HomeUiState(
                saved.toHttpRequest(),
                response
            )
        }
    }

    fun loadRequestFromCollection(requestId: Int) {
        viewModelScope.launch(dispatcher) {
            val saved = collectionRepository.getCollectionRequest(requestId)

            val response = when {
                saved.statusCode != null -> Loadable.Success(saved.toHttpResponse())
                saved.requestUrl == null -> Loadable.Empty
                else -> Loadable.Error(saved.toHttpResponse().response)
            }

            _uiState.value = HomeUiState(
                saved.toHttpRequest(),
                response
            )
        }
    }

    fun updateCollectionRequest(collectionId: String, request: Request) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.updateCollectionRequest(collectionId, request)
        }
    }
}