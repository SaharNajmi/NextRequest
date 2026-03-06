package com.example.nextrequest.socket.di

import com.example.nextrequest.core.data.di.IoDispatcher
import com.example.nextrequest.socket.data.network.WebSocketRepositoryImp
import com.example.nextrequest.socket.domain.repository.WebSocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class WebSocketModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

    @Provides
    fun provideWebSocketRepository(
        client: OkHttpClient,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): WebSocketRepository = WebSocketRepositoryImp(client, dispatcher)
}