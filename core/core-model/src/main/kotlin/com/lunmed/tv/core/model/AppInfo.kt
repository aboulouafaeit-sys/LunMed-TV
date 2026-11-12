package com.lunmed.tv.core.model

/**
 * Domain model representing an installed launchable application on the TV device.
 */
data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val categoryId: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val isTvApp: Boolean = false,
    val isSystemApp: Boolean = false,
    val customOrder: Int = Int.MAX_VALUE,
    val lastLaunchedEpochMs: Long = 0L,
    val launchCount: Int = 0,
)

/**
 * Domain model for user-defined or system-mapped application categories.
 */
data class AppCategory(
    val id: String,
    val name: String,
    val sortOrder: Int = 0,
    val isSystemCategory: Boolean = false,
)

