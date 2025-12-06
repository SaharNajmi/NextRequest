package com.example.nextrequest.collection.di

import com.example.nextrequest.collection.data.dao.CollectionDao
import com.example.nextrequest.core.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CollectionDatabaseModule {

    @Provides
    fun provideCollocationDao(db: AppDatabase): CollectionDao = db.collectionDao()
}