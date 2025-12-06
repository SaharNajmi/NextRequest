package com.example.nextrequest.history.domain.repository

import com.example.nextrequest.history.domain.model.History

interface HistoryRepository {
    suspend fun getAllHistories(): List<History>

    suspend fun insertHistoryRequest(history: History)

    suspend fun updateHistoryRequest(historyItem: History)

    suspend fun deleteHistoryRequest(historyId: Int)

    suspend fun deleteHistoriesRequest(ids: List<Int>)

    suspend fun getHistoryRequest(historyId: Int): History
}