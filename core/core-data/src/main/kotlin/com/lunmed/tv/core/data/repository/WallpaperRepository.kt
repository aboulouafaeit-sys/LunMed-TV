package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.LunmedWallpapers
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

    override fun getBuiltInWallpapers(): List<LunmedWallpaper> = LunmedWallpapers.BUILT_IN

    override fun getWallpaperById(id: String): LunmedWallpaper? =
        LunmedWallpapers.BUILT_IN.find { it.id == id }

    override fun getSelectedWallpaper(): Flow<LunmedWallpaper> =
        preferencesRepository.preferences.map { prefs ->
            LunmedWallpapers.getById(prefs.selectedWallpaperId)
        }

    override suspend fun selectWallpaper(id: String) {
        preferencesRepository.setSelectedWallpaperId(id)
    }
}

