package com.example.nextrequest.history.domain.repository

import com.example.nextrequest.history.domain.model.History

interface HistoryRepository {
    suspend fun getAllHistories(): List<History>

    suspend fun insertHistoryHttp(history: History)

    suspend fun updateHistory(historyItem: History)

    suspend fun deleteHistory(historyId: Int)

    suspend fun deleteHistories(ids: List<Int>)

    suspend fun getHistory(historyId: Int): History
}