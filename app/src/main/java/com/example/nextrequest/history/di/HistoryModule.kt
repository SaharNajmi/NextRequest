package com.example.nextrequest.history.di

import com.example.nextrequest.core.data.di.IoDispatcher
import com.example.nextrequest.history.data.dao.HistoryRequestDao
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
    fun provideHistoryRequestRepository(
        historyRequestDao: HistoryRequestDao,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): HistoryRepository =
        HistoryRepositoryImp(historyRequestDao, dispatcher)

}