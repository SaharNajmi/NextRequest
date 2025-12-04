package com.example.postman

import android.content.Context
import androidx.room.Room
import com.example.postman.collection.data.dao.CollectionDao
import com.example.postman.core.data.db.AppDatabase
import com.example.postman.core.data.di.DatabaseModule
import com.example.postman.history.data.dao.HistoryRequestDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseModuleTest {

    @Provides
    @Singleton
    fun provideTestDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryRequestDao =
        db.historyRequestDao()

    @Provides
    fun provideCollocationDao(db: AppDatabase): CollectionDao = db.collectionDao()
}