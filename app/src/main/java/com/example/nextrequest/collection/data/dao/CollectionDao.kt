package com.example.nextrequest.collection.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nextrequest.collection.data.entity.CollectionEntity
import com.example.nextrequest.collection.data.entity.RequestEntity

@Dao
interface CollectionDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Query("SELECT * FROM collections")
    suspend fun getAllCollections(): List<CollectionEntity>

    @Query("SELECT requestName FROM requests WHERE id = :requestId")
    suspend fun getRequestName(requestId: Int): String

    @Update
    suspend fun updateCollection(collection: CollectionEntity)

    @Update
    suspend fun updateCollectionRequest(request: RequestEntity)

    @Query("DELETE FROM collections WHERE collectionId = :collectionId")
    suspend fun deleteCollection(collectionId: String)

    @Insert
    suspend fun insertRequestToCollection(request: RequestEntity)

    @Query("SELECT * FROM requests WHERE collectionId = :collectionId")
    suspend fun getCollectionRequests(collectionId: String): List<RequestEntity>

    @Query("SELECT * FROM requests WHERE id= :requestId ")
    suspend fun getCollectionRequest(requestId: Int): RequestEntity

    @Query("DELETE FROM requests WHERE id = :requestId")
    suspend fun deleteRequestFromCollection(requestId: Int)

    @Query("UPDATE requests SET requestName = :requestName WHERE id = :requestId")
    suspend fun changeRequestName(requestId: Int, requestName: String)
}