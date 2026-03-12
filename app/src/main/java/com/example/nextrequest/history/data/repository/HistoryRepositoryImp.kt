package com.example.nextrequest.history.data.repository

import com.example.nextrequest.history.data.dao.HistoryDao
import com.example.nextrequest.history.data.mapper.toDomain
import com.example.nextrequest.history.data.mapper.toEntity
import com.example.nextrequest.history.domain.model.History
import com.example.nextrequest.history.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class HistoryRepositoryImp(
    private val historyDao: HistoryDao,
    private val dispatcher: CoroutineDispatcher
) : HistoryRepository {
    override suspend fun getAllHistories(): List<History> =
        withContext(dispatcher) { historyDao.getAllHistories().map { it.toDomain() } }

    override suspend fun insertHistoryHttp(history: History) =
        withContext(dispatcher) { historyDao.insertHistory(history.toEntity()) }


    override suspend fun updateHistory(history: History) =
        withContext(dispatcher) { historyDao.updateHistory(history.toEntity()) }

    override suspend fun deleteHistory(historyId: Int) =
        withContext(dispatcher) { historyDao.deleteHistory(historyId) }

    override suspend fun deleteHistories(ids: List<Int>) =
        withContext(dispatcher) { historyDao.deleteHistories(ids) }

    override suspend fun getHistory(historyId: Int): History =
        withContext(dispatcher) { historyDao.getHistory(historyId).toDomain() }

}