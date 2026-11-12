package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultWallpaperRepositoryTest {

    private class TestPreferencesRepository : PreferencesRepository {
        val state = MutableStateFlow(LauncherPreferences())
        override val preferences: Flow<LauncherPreferences> = state

        override suspend fun setAccentPreset(preset: AccentPreset) {}
        override suspend fun setCardSize(preset: CardSizePreset) {}
        override suspend fun setDensity(density: InterfaceDensity) {}
        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {}
        override suspend fun setShowClock(show: Boolean) {}
        override suspend fun setShowDeviceIndicators(show: Boolean) {}
        override suspend fun setShowRecentApps(show: Boolean) {}
        override suspend fun setLanguageTag(tag: String) {}
        override suspend fun setSelectedWallpaperId(wallpaperId: String) {
            state.value = state.value.copy(selectedWallpaperId = wallpaperId)
        }
        override suspend fun resetToDefaults() {}
    }

    @Test
    fun getBuiltInWallpapers_returns5CinematicPresets() {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)
        val wallpapers = repo.getBuiltInWallpapers()

        assertEquals(5, wallpapers.size)
        assertTrue(wallpapers.any { it.id == "lunmed_obsidian_01" })
        assertTrue(wallpapers.any { it.id == "lunmed_cosmic_gold_02" })
        assertTrue(wallpapers.any { it.id == "lunmed_cyber_cyan_03" })
        assertTrue(wallpapers.any { it.id == "lunmed_aurora_silver_04" })
        assertTrue(wallpapers.any { it.id == "lunmed_emerald_dusk_05" })
    }

    @Test
    fun getWallpaperById_findsKnownId() {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)

        val wallpaper = repo.getWallpaperById("lunmed_cyber_cyan_03")
        assertNotNull(wallpaper)
        assertEquals("Cyber Azure", wallpaper?.title)
    }

    @Test
    fun selectWallpaper_updatesSelectedStream() = runTest {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)

        val initial = repo.getSelectedWallpaper().first()
        assertEquals("lunmed_obsidian_01", initial.id)

        repo.selectWallpaper("lunmed_emerald_dusk_05")
        val updated = repo.getSelectedWallpaper().first()
        assertEquals("lunmed_emerald_dusk_05", updated.id)
        assertEquals("Obsidian Jade", updated.title)
    }
}

