package com.example.nextrequest.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nextrequest.collection.data.dao.CollectionDao
import com.example.nextrequest.history.data.dao.HistoryRequestDao
import com.example.nextrequest.collection.data.entity.CollectionEntity
import com.example.nextrequest.history.data.entity.HistoryEntity
import com.example.nextrequest.collection.data.entity.RequestEntity

@Database(
    entities = [HistoryEntity::class, CollectionEntity::class, RequestEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyRequestDao(): HistoryRequestDao
    abstract fun collectionDao(): CollectionDao
}