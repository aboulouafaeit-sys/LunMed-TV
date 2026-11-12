package com.lunmed.tv.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lunmed.tv.core.database.entity.AppCategoryEntity
import com.lunmed.tv.core.database.entity.AppMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppMetadataDao {
    @Query("SELECT * FROM app_metadata ORDER BY customOrder ASC")
    fun observeAll(): Flow<List<AppMetadataEntity>>

    @Query("SELECT * FROM app_metadata WHERE packageName = :packageName")
    suspend fun getByPackage(packageName: String): AppMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AppMetadataEntity)

    @Query("DELETE FROM app_metadata WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
}

@Dao
interface AppCategoryDao {
    @Query("SELECT * FROM app_categories ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<AppCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<AppCategoryEntity>)
}

