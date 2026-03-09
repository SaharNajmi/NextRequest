package com.example.nextrequest.socket.di

import com.example.nextrequest.socket.data.network.WebSocketRepositoryImp
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class WebSocketModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

    @Provides
    fun provideWebSocketRepository(
        client: OkHttpClient,
        coroutineScope: CoroutineScope,
    ): WebSocketRepository = WebSocketRepositoryImp(client, coroutineScope)
}