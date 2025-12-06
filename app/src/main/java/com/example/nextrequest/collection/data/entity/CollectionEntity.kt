package com.example.nextrequest.collection.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.KeyValueList

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey
    val collectionId: String,
    val collectionName: String,
)

@Entity(
    tableName = "requests",
    // primaryKeys = ["collectionId", "id"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE // if parent deleted -> children auto deleted
        )
    ],
    indices = [Index("collectionId")]
)
@Suppress("ArrayInDataClass")
data class RequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val collectionId: String,
    val requestName: String,
    val requestUrl: String?,
    val httpMethod: HttpMethod,
    val response: String,
    val imageResponse: ByteArray? = null,
    val createdAt: Long,
    val statusCode: Int?,
    val body: String? = null,
    val headers: KeyValueList? = null,
)