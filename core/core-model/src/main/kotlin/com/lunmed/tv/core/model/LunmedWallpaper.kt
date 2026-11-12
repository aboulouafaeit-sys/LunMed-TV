package com.lunmed.tv.core.model

/**
 * Cinematic wallpaper definition for LUNMED TV luxury backdrop.
 */
data class LunmedWallpaper(
    val id: String,
    val title: String,
    val description: String,
    val primaryColorArgb: Long,
    val secondaryColorArgb: Long,
    val ambientGlowArgb: Long,
    val isCustom: Boolean = false,
    val customUri: String? = null,
)

