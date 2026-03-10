package com.example.nextrequest.socket.di

import com.example.nextrequest.core.data.di.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(@IoDispatcher dispatcher: CoroutineDispatcher): CoroutineScope {
        return CoroutineScope(dispatcher)
    }
}