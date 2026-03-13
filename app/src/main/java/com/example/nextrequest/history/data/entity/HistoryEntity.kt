package com.example.nextrequest.history.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nextrequest.history.domain.model.RequestType

@Entity(tableName = "histories")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: RequestType,
    val data: String,
    val createdAt: Long,
)