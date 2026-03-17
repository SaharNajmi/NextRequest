package com.example.nextrequest.collection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.collection.presentation.model.CollectionUiState
import com.example.nextrequest.core.domain.model.HttpRequest
import com.example.nextrequest.core.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CollectionUiState>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CollectionUiState>>> = _uiState

    fun getCollections() {
        viewModelScope.launch(dispatcher) {
            if (_uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            try {
                val collections = collectionRepository.getAllCollections()
                val currentList = (_uiState.value as? UiState.Success)?.data ?: emptyList()
                _uiState.value = UiState.Success(
                    collections.map { item ->
                        val expanded = currentList
                            .firstOrNull { it.requestCollection.collectionId == item.collectionId }
                            ?.isExpanded ?: false
                        CollectionUiState(item, expanded)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleExpanded(collectionId: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(
            current.map {
                if (it.requestCollection.collectionId == collectionId) it.copy(isExpanded = !it.isExpanded)
                else it
            }
        )
    }

    fun deleteRequestItem(requestId: Int) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.deleteItemFromCollection(requestId)
            getCollections()
        }
    }

    fun deleteCollection(collectionId: String) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.deleteCollection(collectionId)
            getCollections()
        }
    }

    fun createNewCollection() {
        viewModelScope.launch(dispatcher) {
            collectionRepository.insertCollection(RequestCollection())
            getCollections()
        }
    }

    fun createAnEmptyRequest(collectionId: String) {
        viewModelScope.launch(dispatcher) {
            val newItem = CollectionItem.Http(
                requestId = 0,
                requestName = "Http Request",
                request = HttpRequest(
                    requestUrl = ""
                )
            )
            collectionRepository.insertItemToCollection(collectionId, newItem)
            getCollections()
        }
    }

    fun updateCollection(requestCollection: RequestCollection) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.updateCollection(requestCollection)
            getCollections()
        }
    }

    fun changeRequestName(requestId: Int, requestName: String) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.changeRequestName(requestId, requestName)
            getCollections()
        }
    }
}