package com.example.nextrequest.history.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nextrequest.history.data.entity.HistoryEntity

@Dao
interface HistoryRequestDao {
    @Query("SELECT * FROM histories ORDER by id DESC")
    suspend fun getAllHistories(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertHistoryRequest(history: HistoryEntity)

    @Update
    suspend fun updateHistoryRequest(history: HistoryEntity)

    @Query("DELETE FROM histories WHERE id = :historyId")
    suspend fun deleteHistoryRequest(historyId: Int)

    @Query("DELETE FROM histories WHERE id in (:ids)")
    suspend fun deleteHistoriesRequest(ids: List<Int>)

    @Query("SELECT * FROM histories WHERE id= :historyId ")
    suspend fun getHistoryRequest(historyId: Int): HistoryEntity

}