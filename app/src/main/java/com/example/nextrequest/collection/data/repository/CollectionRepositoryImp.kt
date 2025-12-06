package com.example.nextrequest.collection.data.repository

import com.example.nextrequest.collection.data.dao.CollectionDao
import com.example.nextrequest.collection.data.mapper.toDomain
import com.example.nextrequest.collection.data.mapper.toEntity
import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CollectionRepositoryImp(
    private val collectionDao: CollectionDao,
    private val dispatcher: CoroutineDispatcher,
) : CollectionRepository {
    override suspend fun insertCollection(collection: Collection) = withContext(dispatcher) {
        collectionDao.insertCollection(collection.toEntity())
    }

    override suspend fun getAllCollections(): List<Collection> = withContext(dispatcher) {
        collectionDao.getAllCollections().map {
            val requests = getCollectionRequests(it.collectionId)
            it.toDomain(requests)
        }
    }

    override suspend fun getRequestName(requestId: Int): String = withContext(dispatcher) {
        collectionDao.getRequestName(requestId)
    }

    override suspend fun updateCollection(collection: Collection) = withContext(dispatcher) {
        collectionDao.updateCollection(collection.toEntity())
    }

    override suspend fun updateCollectionRequest(
        collectionId: String,
        request: Request,
    ) = withContext(dispatcher) {
        val requestName = collectionDao.getRequestName(request.id)
        collectionDao.updateCollectionRequest(request.toEntity(collectionId, requestName))
    }

    override suspend fun deleteCollection(collectionId: String) = withContext(dispatcher) {
        collectionDao.deleteCollection(collectionId)
    }

    override suspend fun insertRequestToCollection(collectionId: String, request: Request) =
        withContext(dispatcher) {
            collectionDao.insertRequestToCollection(request.toEntity(collectionId))
        }

    override suspend fun getCollectionRequests(collectionId: String): List<Request> =
        withContext(dispatcher) {
            collectionDao.getCollectionRequests(collectionId).map { it.toDomain() }
        }

    override suspend fun getCollectionRequest(requestId: Int): Request = withContext(dispatcher) {
        collectionDao.getCollectionRequest(requestId).toDomain()
    }

    override suspend fun deleteRequestFromCollection(requestId: Int) = withContext(dispatcher) {
        collectionDao.deleteRequestFromCollection(requestId)
    }

    override suspend fun changeRequestName(requestId: Int, requestName: String) =
        withContext(dispatcher) {
            collectionDao.changeRequestName(requestId, requestName)
        }
}