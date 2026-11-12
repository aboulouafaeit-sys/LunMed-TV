package com.lunmed.tv.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_metadata")
data class AppMetadataEntity(
    @PrimaryKey
    val packageName: String,
    val categoryId: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val customOrder: Int = Int.MAX_VALUE,
    val lastLaunchedEpochMs: Long = 0L,
    val launchCount: Int = 0,
)

@Entity(tableName = "app_categories")
data class AppCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sortOrder: Int = 0,
    val isSystemCategory: Boolean = false,
)

