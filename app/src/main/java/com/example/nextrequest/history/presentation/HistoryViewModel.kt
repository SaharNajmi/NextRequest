package com.example.nextrequest.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.collection.presentation.model.CollectionEntry
import com.example.nextrequest.core.presentation.UiState
import com.example.nextrequest.history.domain.formatDate
import com.example.nextrequest.history.domain.mapper.getCreatedAt
import com.example.nextrequest.history.domain.mapper.toCollectionItem
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import com.example.nextrequest.history.presentation.model.ExpandableHistoryItem
import com.example.nextrequest.history.presentation.model.HistoryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    val collectionRepository: CollectionRepository,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<HistoryUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<HistoryUiModel>> = _uiState

    fun getHistories() {
        viewModelScope.launch {
            val oldExpandedStates =
                (_uiState.value as? UiState.Success)?.data?.expandedStates?.associateBy { it.dateCreated }
                    ?: emptyMap()
            _uiState.value = UiState.Loading
            try {
                val historiesDeferred =
                    async(dispatcher) { historyRepository.getAllHistories() }
                val collectionsDeferred =
                    async(dispatcher) { collectionRepository.getAllCollections() }

                val histories = historiesDeferred.await()
                val collections = collectionsDeferred.await()

                val grouped: Map<LocalDate, List<HistoryItem>> =
                    histories.groupBy { it.getCreatedAt() }

                val historyEntries = grouped
                    .toSortedMap(compareByDescending { it })
                    .map { (date, histories) ->
                        HistoryEntry(dateCreated = formatDate(date), histories = histories)
                    }

                val expandedStates = grouped.keys.map { date ->
                    oldExpandedStates[formatDate(date)] ?: ExpandableHistoryItem(
                        formatDate(date),
                        false
                    )
                }
                val collectionEntries = collections.map {
                    CollectionEntry(it.collectionId, it.collectionName)
                }.toSet()

                val data = HistoryUiModel(
                    historyEntries = historyEntries,
                    expandedStates = expandedStates,
                    collectionNames = collectionEntries
                )

                _uiState.value = UiState.Success(data)

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun toggleExpanded(dateCreated: String) {
        val current = _uiState.value
        if (current is UiState.Success) {
            val newExpanded = current.data.expandedStates.map { item ->
                if (item.dateCreated == dateCreated) item.copy(isExpanded = !item.isExpanded)
                else item
            }
            val newData = current.data.copy(expandedStates = newExpanded)
            _uiState.value = UiState.Success(newData)
        }
    }

    fun deleteHistory(historyItem: HistoryItem) {
        viewModelScope.launch(dispatcher) {
            val id = when (historyItem) {
                is HistoryItem.Http -> historyItem.id
                is HistoryItem.WebSocket -> historyItem.id
            }
            historyRepository.deleteHistory(id)
            getHistories()
        }
    }

    fun deleteHistories(histories: List<HistoryItem>) {
        viewModelScope.launch(dispatcher) {
            val idsToDelete = histories.map { item ->
                when (item) {
                    is HistoryItem.Http -> item.id
                    is HistoryItem.WebSocket -> item.id
                }
            }
            historyRepository.deleteHistories(idsToDelete)
            getHistories()
        }
    }

    fun getCollections() {
        viewModelScope.launch(dispatcher) {
            try {
                val collections = collectionRepository.getAllCollections()
                val collectionEntries = collections.map {
                    CollectionEntry(it.collectionId, it.collectionName)
                }.toSet()

                val currentData = (_uiState.value as? UiState.Success)?.data
                    ?: HistoryUiModel()

                _uiState.value = UiState.Success(
                    currentData.copy(collectionNames = collectionEntries)
                )

            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    fun addHistoryToCollection(historyItem: HistoryItem, collectionId: String) {
        viewModelScope.launch(dispatcher) {
            collectionRepository.insertItemToCollection(
                collectionId,
                historyItem.toCollectionItem()
            )
        }
    }

    fun addHistoriesToCollection(
        histories: List<HistoryItem>,
        collectionId: String,
    ) {
        val requests = histories.map { it.toCollectionItem() }
        viewModelScope.launch(dispatcher) {
            requests.forEach { collectionRepository.insertItemToCollection(collectionId, it) }
        }
    }

    fun createNewCollection() {
        viewModelScope.launch(dispatcher) {
            collectionRepository.insertCollection(RequestCollection())
            getCollections()
        }
    }
}