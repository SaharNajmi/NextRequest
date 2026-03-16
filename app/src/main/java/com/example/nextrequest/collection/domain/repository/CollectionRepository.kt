package com.example.nextrequest.collection.domain.repository

import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.model.CollectionItem

interface CollectionRepository {
    suspend fun getAllCollections(): List<RequestCollection>
    suspend fun insertCollection(requestCollection: RequestCollection)
    suspend fun updateCollection(requestCollection: RequestCollection)
    suspend fun deleteCollection(collectionId: String)
    suspend fun insertItemToCollection(collectionId: String, item: CollectionItem)
    suspend fun updateCollectionItem(collectionId: String, collectionItem: CollectionItem)
    suspend fun getCollectionItems(collectionId: String): List<CollectionItem>
    suspend fun getCollectionItem(requestId: Int): CollectionItem
    suspend fun deleteItemFromCollection(itemId: Int)
    suspend fun getRequestName(itemId: Int): String
    suspend fun changeRequestName(itemId: Int, requestName: String)
}