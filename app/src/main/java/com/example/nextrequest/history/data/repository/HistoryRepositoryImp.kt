package com.example.nextrequest.history.data.repository

import com.example.nextrequest.history.data.dao.HistoryRequestDao
import com.example.nextrequest.history.data.mapper.toDomain
import com.example.nextrequest.history.data.mapper.toEntity
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class HistoryRepositoryImp(
    private val historyRequestDao: HistoryRequestDao,
    private val dispatcher: CoroutineDispatcher
) : HistoryRepository {
    override suspend fun getAllHistories(): List<History> =
        withContext(dispatcher) { historyRequestDao.getAllHistories().map { it.toDomain() } }

    override suspend fun insertHistoryRequest(history: History) =
        withContext(dispatcher) { historyRequestDao.insertHistoryRequest(history.toEntity()) }

    override suspend fun updateHistoryRequest(history: History) =
        withContext(dispatcher) { historyRequestDao.updateHistoryRequest(history.toEntity()) }

    override suspend fun deleteHistoryRequest(historyId: Int) =
        withContext(dispatcher) { historyRequestDao.deleteHistoryRequest(historyId) }

    override suspend fun deleteHistoriesRequest(ids: List<Int>) =
        withContext(dispatcher) { historyRequestDao.deleteHistoriesRequest(ids) }

    override suspend fun getHistoryRequest(historyId: Int): History =
        withContext(dispatcher) { historyRequestDao.getHistoryRequest(historyId).toDomain() }

}