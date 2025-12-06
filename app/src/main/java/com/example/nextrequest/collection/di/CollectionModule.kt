package com.example.nextrequest.collection.di

import com.example.nextrequest.collection.data.dao.CollectionDao
import com.example.nextrequest.collection.data.repository.CollectionRepositoryImp
import com.example.nextrequest.collection.domain.repository.CollectionRepository
import com.example.nextrequest.core.data.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {
    @Provides
    fun provideCollectionRepository(
        collectionDao: CollectionDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): CollectionRepository =
        CollectionRepositoryImp(collectionDao, dispatcher)
}