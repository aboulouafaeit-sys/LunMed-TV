package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.model.LunmedWallpaper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface WallpaperRepository {
    fun getBuiltInWallpapers(): List<LunmedWallpaper>
    fun getWallpaperById(id: String): LunmedWallpaper?
    fun getSelectedWallpaper(): Flow<LunmedWallpaper>
    suspend fun selectWallpaper(id: String)
}

@Singleton
class DefaultWallpaperRepository @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : WallpaperRepository {

    private val builtInWallpapers: List<LunmedWallpaper> = listOf(
        LunmedWallpaper(
            id = "lunmed_obsidian_01",
            title = "Obsidian Noir",
            description = "Signature velvet black obsidian canvas with ambient champagne warmth.",
            primaryColorArgb = 0xFF050505,
            secondaryColorArgb = 0xFF0E0E10,
            ambientGlowArgb = 0xFFD4AF37,
        ),
        LunmedWallpaper(
            id = "lunmed_cosmic_gold_02",
            title = "Cosmic Amber",
            description = "Deep celestial obsidian with rich amber auroral radiance.",
            primaryColorArgb = 0xFF0A0704,
            secondaryColorArgb = 0xFF1A1208,
            ambientGlowArgb = 0xFFFFB300,
        ),
        LunmedWallpaper(
            id = "lunmed_cyber_cyan_03",
            title = "Cyber Azure",
            description = "Futuristic cryogenic deep indigo with electric ice cyan horizon.",
            primaryColorArgb = 0xFF040A10,
            secondaryColorArgb = 0xFF081824,
            ambientGlowArgb = 0xFF00E5FF,
        ),
        LunmedWallpaper(
            id = "lunmed_aurora_silver_04",
            title = "Nordic Platinum",
            description = "Subtle metallic platinum mist over infinite obsidian silence.",
            primaryColorArgb = 0xFF08080A,
            secondaryColorArgb = 0xFF141418,
            ambientGlowArgb = 0xFFE0E0E6,
        ),
        LunmedWallpaper(
            id = "lunmed_emerald_dusk_05",
            title = "Obsidian Jade",
            description = "Regal deep emerald glow with royal dark velvet undertones.",
            primaryColorArgb = 0xFF040C08,
            secondaryColorArgb = 0xFF0A1A12,
            ambientGlowArgb = 0xFF00E676,
        ),
    )

    override fun getBuiltInWallpapers(): List<LunmedWallpaper> = builtInWallpapers

    override fun getWallpaperById(id: String): LunmedWallpaper? =
        builtInWallpapers.find { it.id == id }

    override fun getSelectedWallpaper(): Flow<LunmedWallpaper> =
        preferencesRepository.preferences.map { prefs ->
            getWallpaperById(prefs.selectedWallpaperId) ?: builtInWallpapers.first()
        }

    override suspend fun selectWallpaper(id: String) {
        preferencesRepository.setSelectedWallpaperId(id)
    }
}

