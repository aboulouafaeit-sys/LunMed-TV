package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.datastore.LunmedPreferencesDataSource
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    val preferences: Flow<LauncherPreferences>
    suspend fun setAccentPreset(preset: AccentPreset)
    suspend fun setCardSize(preset: CardSizePreset)
    suspend fun setDensity(density: InterfaceDensity)
    suspend fun setAnimationIntensity(intensity: AnimationIntensity)
    suspend fun setShowClock(show: Boolean)
    suspend fun setShowDeviceIndicators(show: Boolean)
    suspend fun setShowRecentApps(show: Boolean)
    suspend fun setLanguageTag(tag: String)
    suspend fun setSelectedWallpaperId(wallpaperId: String)
    suspend fun setShowStartupSplash(show: Boolean)
    suspend fun resetToDefaults()
}

@Singleton
class DefaultPreferencesRepository @Inject constructor(
    private val dataSource: LunmedPreferencesDataSource,
) : PreferencesRepository {

    override val preferences: Flow<LauncherPreferences> = dataSource.preferences

    override suspend fun setAccentPreset(preset: AccentPreset) {
        dataSource.setAccentPreset(preset)
    }

    override suspend fun setCardSize(preset: CardSizePreset) {
        dataSource.setCardSize(preset)
    }

    override suspend fun setDensity(density: InterfaceDensity) {
        dataSource.setDensity(density)
    }

    override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
        dataSource.setAnimationIntensity(intensity)
    }

    override suspend fun setShowClock(show: Boolean) {
        dataSource.setShowClock(show)
    }

    override suspend fun setShowDeviceIndicators(show: Boolean) {
        dataSource.setShowDeviceIndicators(show)
    }

    override suspend fun setShowRecentApps(show: Boolean) {
        dataSource.setShowRecentApps(show)
    }

    override suspend fun setLanguageTag(tag: String) {
        dataSource.setLanguageTag(tag)
    }

    override suspend fun setSelectedWallpaperId(wallpaperId: String) {
        dataSource.setSelectedWallpaperId(wallpaperId)
    }

    override suspend fun setShowStartupSplash(show: Boolean) {
        dataSource.setShowStartupSplash(show)
    }

    override suspend fun resetToDefaults() {
        dataSource.resetToDefaults()
    }
}

