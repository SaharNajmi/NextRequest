package com.example.nextrequest.collection.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nextrequest.collection.data.entity.CollectionEntity
import com.example.nextrequest.collection.data.entity.CollectionItemEntity

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Query("SELECT * FROM collections")
    suspend fun getAllCollections(): List<CollectionEntity>

    @Query("SELECT requestName FROM collection_items WHERE id = :requestId")
    suspend fun getRequestName(requestId: Int): String

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Update
    suspend fun updateCollectionItem(request: CollectionItemEntity)

    @Query("DELETE FROM collections WHERE collectionId = :collectionId")
    suspend fun deleteCollection(collectionId: String)

    @Insert
    suspend fun insertRequestToCollection(request: CollectionItemEntity)

    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId")
    suspend fun getCollectionRequests(collectionId: String): List<CollectionItemEntity>

    @Query("SELECT * FROM collection_items WHERE id= :requestId ")
    suspend fun getCollectionRequest(requestId: Int): CollectionItemEntity

    @Query("DELETE FROM collection_items WHERE id = :requestId")
    suspend fun deleteRequestFromCollection(requestId: Int)

    @Query("UPDATE collection_items SET requestName = :requestName WHERE id = :requestId")
    suspend fun changeRequestName(requestId: Int, requestName: String)
}