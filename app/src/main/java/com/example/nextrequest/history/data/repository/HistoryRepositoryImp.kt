package com.example.nextrequest.history.data.repository

import com.example.nextrequest.history.data.dao.HistoryDao
import com.example.nextrequest.history.domain.mapper.toDomain
import com.example.nextrequest.history.domain.mapper.toEntity
import com.example.nextrequest.history.domain.model.HistoryItem
import com.example.nextrequest.history.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class HistoryRepositoryImp(
    private val historyDao: HistoryDao,
    private val dispatcher: CoroutineDispatcher,
) : HistoryRepository {
    override suspend fun getAllHistories(): List<HistoryItem> =
        withContext(dispatcher) { historyDao.getAllHistories().map { it.toDomain() } }

    override suspend fun insertHistory(historyItem: HistoryItem) =
        withContext(dispatcher) { historyDao.insertHistory(historyItem.toEntity()) }

    override suspend fun updateHistory(historyItem: HistoryItem) =
        withContext(dispatcher) { historyDao.updateHistory(historyItem.toEntity()) }

    override suspend fun deleteHistory(historyId: Int) =
        withContext(dispatcher) { historyDao.deleteHistory(historyId) }

    override suspend fun deleteHistories(ids: List<Int>) =
        withContext(dispatcher) { historyDao.deleteHistories(ids) }

    override suspend fun getHistory(historyId: Int): HistoryItem =
        withContext(dispatcher) { historyDao.getHistory(historyId).toDomain() }

}