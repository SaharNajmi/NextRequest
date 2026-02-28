package com.example.nextrequest.socket.di

import com.example.nextrequest.socket.data.network.WebSocketRepositoryImp
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class WebSocketModule {
    @Provides
    fun provideWebSocketRepository(): WebSocketRepository = WebSocketRepositoryImp()
}