package com.example.nextrequest.core.data.di

import com.example.nextrequest.core.data.network.ApiServiceImp
import com.example.nextrequest.core.domain.repository.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import javax.inject.Singleton
import io.ktor.serialization.gson.gson

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideApiService(httpClient: HttpClient): ApiService = ApiServiceImp(httpClient)

    @Singleton
    @Provides
    fun provideHttpClient() = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                serializeNulls()
            }
        }
    }
}