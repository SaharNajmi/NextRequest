package com.example.nextrequest.history.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.models.HttpMethod

@Suppress("ArrayInDataClass")
@Entity(tableName = "histories")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val requestUrl: String,
    val httpMethod: HttpMethod,
    val response: String,
    val imageResponse: ByteArray?=null,
    val createdAt: Long,
    val statusCode : Int?,
    val body: String? = null,
    val headers: KeyValueList? = null
)