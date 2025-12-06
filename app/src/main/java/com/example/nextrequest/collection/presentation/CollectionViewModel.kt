package com.example.nextrequest.collection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.collection.presentation.model.CollectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _collections =
        MutableStateFlow<List<CollectionUiState>>(emptyList())
    val collections: StateFlow<List<CollectionUiState>> = _collections

    fun getCollections() {
        viewModelScope.launch(dispatcher) {
            _collections.value = collectionRepository.getAllCollections().map { item ->
                val expanded =
                    _collections.value.firstOrNull() { it.collection.collectionId == item.collectionId }?.isExpanded
                        ?: false
                CollectionUiState(item, expanded)
            }
        }
    }

    fun toggleExpanded(collectionId: String) {
        _collections.value = _collections.value.map {
            if (it.collection.collectionId == collectionId) {
                it.copy(isExpanded = !it.isExpanded)
            } else {
                it
            }
        }
    }

    fun deleteRequestItem(requestId: Int) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.deleteRequestFromCollection(requestId)
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
            collectionRepository.insertCollection(Collection())
            getCollections()
        }
    }

    fun createAnEmptyRequest(collectionId: String) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.insertRequestToCollection(collectionId, Request())
            getCollections()
        }
    }

    fun updateCollection(collection: Collection) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.updateCollection(collection)
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