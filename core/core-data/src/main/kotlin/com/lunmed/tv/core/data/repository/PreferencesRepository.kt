package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.datastore.LunmedPreferencesDataSource
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.LauncherPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesRepository {
    val preferences: Flow<LauncherPreferences>
    suspend fun setAccentPreset(preset: AccentPreset)
    suspend fun setAnimationIntensity(intensity: AnimationIntensity)
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

    override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
        dataSource.setAnimationIntensity(intensity)
    }

    override suspend fun resetToDefaults() {
        dataSource.resetToDefaults()
    }
}

