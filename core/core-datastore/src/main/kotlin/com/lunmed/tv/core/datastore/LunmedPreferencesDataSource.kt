package com.lunmed.tv.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LunmedPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val ACCENT_PRESET = stringPreferencesKey("accent_preset")
        val CARD_SIZE = stringPreferencesKey("card_size")
        val DENSITY = stringPreferencesKey("density")
        val ANIMATION_INTENSITY = stringPreferencesKey("animation_intensity")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val SHOW_DEVICE_INDICATORS = booleanPreferencesKey("show_device_indicators")
        val SHOW_RECENT_APPS = booleanPreferencesKey("show_recent_apps")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val SELECTED_WALLPAPER_ID = stringPreferencesKey("selected_wallpaper_id")
    }

    val preferences: Flow<LauncherPreferences> = dataStore.data.map { prefs ->
        LauncherPreferences(
            accentPreset = prefs[Keys.ACCENT_PRESET]?.let { key ->
                AccentPreset.entries.firstOrNull { it.key == key }
            } ?: AccentPreset.CHAMPAGNE_GOLD,
            cardSize = prefs[Keys.CARD_SIZE]?.let { key ->
                CardSizePreset.entries.firstOrNull { it.key == key }
            } ?: CardSizePreset.STANDARD,
            density = prefs[Keys.DENSITY]?.let { key ->
                InterfaceDensity.entries.firstOrNull { it.key == key }
            } ?: InterfaceDensity.COMFORTABLE,
            animationIntensity = prefs[Keys.ANIMATION_INTENSITY]?.let { key ->
                AnimationIntensity.entries.firstOrNull { it.key == key }
            } ?: AnimationIntensity.FULL,
            showClock = prefs[Keys.SHOW_CLOCK] ?: true,
            showDeviceIndicators = prefs[Keys.SHOW_DEVICE_INDICATORS] ?: true,
            showRecentApps = prefs[Keys.SHOW_RECENT_APPS] ?: true,
            languageTag = prefs[Keys.LANGUAGE_TAG] ?: "system",
            selectedWallpaperId = prefs[Keys.SELECTED_WALLPAPER_ID] ?: "lunmed_obsidian_01",
        )
    }

    suspend fun setAccentPreset(preset: AccentPreset) {
        dataStore.edit { it[Keys.ACCENT_PRESET] = preset.key }
    }

    suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
        dataStore.edit { it[Keys.ANIMATION_INTENSITY] = intensity.key }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}

