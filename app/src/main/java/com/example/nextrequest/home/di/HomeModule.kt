package com.example.nextrequest.home.di

import com.example.nextrequest.core.domain.repository.ApiService
import com.example.nextrequest.home.data.repository.HomeRepositoryImp
import com.example.nextrequest.home.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    fun provideHomeRepository(
        apiService: ApiService,
    ): HomeRepository = HomeRepositoryImp(apiService)
}