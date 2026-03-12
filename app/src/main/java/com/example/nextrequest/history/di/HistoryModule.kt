package com.example.nextrequest.history.di

import com.example.nextrequest.core.data.di.IoDispatcher
import com.example.nextrequest.history.data.dao.HistoryDao
import com.example.nextrequest.history.data.repository.HistoryRepositoryImp
import com.example.nextrequest.history.domain.repository.HistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher


@Module
@InstallIn(SingletonComponent::class)
object HistoryModule {

    @Provides
    fun provideHistoryRepository(
        historyDao: HistoryDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): HistoryRepository =
        HistoryRepositoryImp(historyDao, dispatcher)

}