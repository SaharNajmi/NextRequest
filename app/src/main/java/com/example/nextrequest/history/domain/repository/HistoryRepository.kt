package com.example.nextrequest.history.domain.repository

import com.example.nextrequest.history.domain.model.HistoryItem

interface HistoryRepository {
    suspend fun getAllHistories(): List<HistoryItem>

    suspend fun insertHistory(historyItem: HistoryItem)

    suspend fun updateHistory(historyItem: HistoryItem)

    suspend fun deleteHistory(historyId: Int)

    suspend fun deleteHistories(ids: List<Int>)

    suspend fun getHistory(historyId: Int): HistoryItem
}