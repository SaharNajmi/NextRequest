package com.example.nextrequest.collection.data.repository

import com.example.nextrequest.collection.data.dao.CollectionDao
import com.example.nextrequest.collection.data.mapper.toDomain
import com.example.nextrequest.collection.data.mapper.toEntity
import com.example.nextrequest.collection.domain.model.RequestCollection
import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CollectionRepositoryImp(
    private val collectionDao: CollectionDao,
    private val dispatcher: CoroutineDispatcher,
) : CollectionRepository {
    override suspend fun insertCollection(requestCollection: RequestCollection) = withContext(dispatcher) {
        collectionDao.insertCollection(requestCollection.toEntity())
    }

    override suspend fun getAllCollections(): List<RequestCollection> = withContext(dispatcher) {
        collectionDao.getAllCollections().map {
            val requests = getCollectionItems(it.collectionId)
            it.toDomain(requests)
        }
    }

    override suspend fun getRequestName(itemId: Int): String = withContext(dispatcher) {
        collectionDao.getRequestName(itemId)
    }

    override suspend fun updateCollection(requestCollection: RequestCollection) = withContext(dispatcher) {
        collectionDao.updateCollection(requestCollection.toEntity())
    }

    override suspend fun updateCollectionItem(
        collectionId: String,
        collectionItem: CollectionItem,
    ) = withContext(dispatcher) {
        val requestName = collectionDao.getRequestName(collectionItem.requestId)
        collectionDao.updateCollectionItem(collectionItem.toEntity(collectionId, requestName))
    }

    override suspend fun deleteCollection(collectionId: String) = withContext(dispatcher) {
        collectionDao.deleteCollection(collectionId)
    }

    override suspend fun insertItemToCollection(collectionId: String, item: CollectionItem) =
        withContext(dispatcher) {
            collectionDao.insertRequestToCollection(item.toEntity(collectionId))
        }

    override suspend fun getCollectionItems(collectionId: String): List<CollectionItem> =
        withContext(dispatcher) {
            collectionDao.getCollectionRequests(collectionId).map { it.toDomain() }
        }

    override suspend fun getCollectionItem(requestId: Int): CollectionItem = withContext(dispatcher) {
        collectionDao.getCollectionRequest(requestId).toDomain()
    }

    override suspend fun deleteItemFromCollection(itemId: Int) = withContext(dispatcher) {
        collectionDao.deleteRequestFromCollection(itemId)
    }

    override suspend fun changeRequestName(itemId: Int, requestName: String) =
        withContext(dispatcher) {
            collectionDao.changeRequestName(itemId, requestName)
        }
}