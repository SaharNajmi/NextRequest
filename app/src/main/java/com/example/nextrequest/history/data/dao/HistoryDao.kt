package com.example.nextrequest.history.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nextrequest.history.data.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM histories ORDER by id DESC")
    suspend fun getAllHistories(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: HistoryEntity)

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Query("DELETE FROM histories WHERE id = :historyId")
    suspend fun deleteHistory(historyId: Int)

    @Query("DELETE FROM histories WHERE id in (:ids)")
    suspend fun deleteHistories(ids: List<Int>)

    @Query("SELECT * FROM histories WHERE id= :historyId ")
    suspend fun getHistory(historyId: Int): HistoryEntity

}