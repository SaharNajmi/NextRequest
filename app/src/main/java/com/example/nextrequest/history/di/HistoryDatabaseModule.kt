package com.example.nextrequest.history.di

import com.example.nextrequest.core.data.db.AppDatabase
import com.example.nextrequest.history.data.dao.HistoryRequestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HistoryDatabaseModule {

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryRequestDao =
        db.historyRequestDao()
}