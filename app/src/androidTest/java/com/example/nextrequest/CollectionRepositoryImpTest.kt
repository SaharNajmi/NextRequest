package com.example.nextrequest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.collection.data.repository.CollectionRepositoryImp
import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CollectionRepositoryImpTest {

    lateinit var collectionRepository: CollectionRepository
    private lateinit var db: AppDatabase
    private var collectionId = "123"

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

       val dao = db.collectionDao()
        collectionRepository = CollectionRepositoryImp(dao, Dispatchers.IO)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun createOneCollectionItem() {
        collectionRepository.insertCollection(Collection(collectionId = collectionId))
        collectionRepository.insertRequestToCollection(
            collectionId,
            Request(11, requestUrl = "test url1")
        )
        collectionRepository.insertRequestToCollection(
            collectionId,
            Request(22, requestUrl = "test url2")
        )
    }

    @Test
    fun insertCollection_insertsCollectionIntoDatabase() = runTest {
        val collection = Collection(collectionId = "1232")
        collectionRepository.insertCollection(collection)

        val all = collectionRepository.getAllCollections()
        assertEquals(1, all.size)
        assertEquals("1232", all[0].collectionId)
    }

    @Test
    fun getAllCollections_returnsAllCollectionsWithRequests() = runTest {
        createOneCollectionItem()
        assertEquals(collectionRepository.getAllCollections().size, 1)
        assertEquals(collectionRepository.getAllCollections().first().requests?.size, 2)
    }

    @Test
    fun getRequestName_returnsCorrectRequestName() = runTest {
        createOneCollectionItem()
        val request = Request(id = 45, requestName = "new name")
        collectionRepository.insertRequestToCollection(
            collectionId,
            request
        )
        val expected = "new name"
        val actual = collectionRepository.getRequestName(request.id)
        assertEquals(expected, actual)
    }

    @Test
    fun updateCollection_updatesExistingCollection() = runTest {
        val newName = "sign up collection"
        val newCollection = Collection(collectionName = "auth collection")
        collectionRepository.insertCollection(newCollection)
        collectionRepository.updateCollection(newCollection.copy(collectionName = newName))
        val actual = collectionRepository.getAllCollections().first().collectionName
        assertEquals(newName, actual)
    }

    @Test
    fun updateCollectionRequest_updatesRequestInCollection() = runTest {
        createOneCollectionItem()
        val request = Request(id = 23, requestUrl = "api.test.dev")
        collectionRepository.insertRequestToCollection(collectionId, request)
        val updatedRequest = request.copy(requestUrl = "api.test2.dev")
        collectionRepository.updateCollectionRequest(
            collectionId,
            updatedRequest
        )
        assertEquals(collectionRepository.getCollectionRequest(request.id), updatedRequest)
    }

    @Test
    fun deleteCollection_removesCollectionById() = runTest {
        createOneCollectionItem()
        collectionRepository.deleteCollection(collectionId)
        assertEquals(collectionRepository.getAllCollections().size, 0)
    }

    @Test
    fun insertRequestToCollection_addsRequestToSpecifiedCollection() = runTest {
        createOneCollectionItem()
        assertEquals(collectionRepository.getAllCollections().first().requests?.size, 2)
    }

    @Test
    fun getCollectionRequests_returnsAllRequestsForCollection() = runTest {
        createOneCollectionItem()
        collectionRepository.getCollectionRequests(collectionId)
        assertEquals(
            collectionRepository.getAllCollections().first().requests,
            collectionRepository.getCollectionRequests(collectionId)
        )
    }

    @Test
    fun getCollectionRequest_returnsRequest() = runTest {
        createOneCollectionItem()
        val request = collectionRepository.getCollectionRequest(11)
        val expected = Request(11, requestUrl = "test url1")
        assertEquals(expected, request)
    }

    @Test
    fun deleteRequestFromCollection_removesRequestById() = runTest {
        createOneCollectionItem()
        collectionRepository.deleteCollection(collectionId)
        val expected = collectionRepository.getAllCollections().size
        assertEquals(expected, 0)
    }

    @Test
    fun changeRequestName_updatesNameOfRequest() = runTest {
        val request = Request(id = 12, requestName = "default name")
        collectionRepository.insertCollection(Collection(collectionId = collectionId))
        collectionRepository.insertRequestToCollection(collectionId, request)
        collectionRepository.changeRequestName(request.id, "new name")
        //val expected= collectionRepository.getAllCollections().first().requests?.first()?.requestName
        val expected = collectionRepository.getRequestName(12)
        assertEquals(expected, "new name")
    }

}