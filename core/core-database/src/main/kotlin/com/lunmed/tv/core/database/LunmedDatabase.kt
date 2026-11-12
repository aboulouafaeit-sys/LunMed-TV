package com.lunmed.tv.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lunmed.tv.core.database.dao.AppCategoryDao
import com.lunmed.tv.core.database.dao.AppMetadataDao
import com.lunmed.tv.core.database.entity.AppCategoryEntity
import com.lunmed.tv.core.database.entity.AppMetadataEntity

@Database(
    entities = [
        AppMetadataEntity::class,
        AppCategoryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class LunmedDatabase : RoomDatabase() {
    abstract fun appMetadataDao(): AppMetadataDao
    abstract fun appCategoryDao(): AppCategoryDao
}

