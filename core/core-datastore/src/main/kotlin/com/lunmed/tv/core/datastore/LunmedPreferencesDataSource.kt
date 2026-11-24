package com.lunmed.tv.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.IconDisplayMode
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
        val ICON_DISPLAY_MODE = stringPreferencesKey("icon_display_mode")
        val DENSITY = stringPreferencesKey("density")
        val ANIMATION_INTENSITY = stringPreferencesKey("animation_intensity")
        val SHOW_CLOCK = booleanPreferencesKey("show_clock")
        val SHOW_DEVICE_INDICATORS = booleanPreferencesKey("show_device_indicators")
        val SHOW_RECENT_APPS = booleanPreferencesKey("show_recent_apps")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val SELECTED_WALLPAPER_ID = stringPreferencesKey("selected_wallpaper_id")
        val SHOW_STARTUP_SPLASH = booleanPreferencesKey("show_startup_splash")
        val PROMPT_DEFAULT_LAUNCHER_ON_STARTUP = booleanPreferencesKey("prompt_default_launcher_on_startup")
    }

    val preferences: Flow<LauncherPreferences> = dataStore.data.map { prefs ->
        LauncherPreferences(
            accentPreset = prefs[Keys.ACCENT_PRESET]?.let { key ->
                AccentPreset.entries.firstOrNull { it.key == key }
            } ?: AccentPreset.CHAMPAGNE_GOLD,
            cardSize = prefs[Keys.CARD_SIZE]?.let { key ->
                CardSizePreset.entries.firstOrNull { it.key == key }
            } ?: CardSizePreset.STANDARD,
            iconDisplayMode = prefs[Keys.ICON_DISPLAY_MODE]?.let { key ->
                IconDisplayMode.entries.firstOrNull { it.key == key }
            } ?: IconDisplayMode.CINEMA_BANNER,
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
            selectedWallpaperId = prefs[Keys.SELECTED_WALLPAPER_ID] ?: "lunmed_aurora_nordic",
            showStartupSplash = prefs[Keys.SHOW_STARTUP_SPLASH] ?: true,
            promptDefaultLauncherOnStartup = prefs[Keys.PROMPT_DEFAULT_LAUNCHER_ON_STARTUP] ?: true,
        )
    }

    suspend fun setAccentPreset(preset: AccentPreset) {
        dataStore.edit { it[Keys.ACCENT_PRESET] = preset.key }
    }

    suspend fun setCardSize(preset: CardSizePreset) {
        dataStore.edit { it[Keys.CARD_SIZE] = preset.key }
    }

    suspend fun setIconDisplayMode(mode: IconDisplayMode) {
        dataStore.edit { it[Keys.ICON_DISPLAY_MODE] = mode.key }
    }

    suspend fun setDensity(density: InterfaceDensity) {
        dataStore.edit { it[Keys.DENSITY] = density.key }
    }

    suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
        dataStore.edit { it[Keys.ANIMATION_INTENSITY] = intensity.key }
    }

    suspend fun setShowClock(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_CLOCK] = show }
    }

    suspend fun setShowDeviceIndicators(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_DEVICE_INDICATORS] = show }
    }

    suspend fun setShowRecentApps(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_RECENT_APPS] = show }
    }

    suspend fun setLanguageTag(tag: String) {
        dataStore.edit { it[Keys.LANGUAGE_TAG] = tag }
    }

    suspend fun setSelectedWallpaperId(wallpaperId: String) {
        dataStore.edit { it[Keys.SELECTED_WALLPAPER_ID] = wallpaperId }
    }

    suspend fun setShowStartupSplash(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_STARTUP_SPLASH] = show }
    }

    suspend fun setPromptDefaultLauncherOnStartup(prompt: Boolean) {
        dataStore.edit { it[Keys.PROMPT_DEFAULT_LAUNCHER_ON_STARTUP] = prompt }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}

