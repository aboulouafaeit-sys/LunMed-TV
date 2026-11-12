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
        override suspend fun setIconDisplayMode(mode: com.lunmed.tv.core.model.IconDisplayMode) {}
        override suspend fun setDensity(density: InterfaceDensity) {}
        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {}
        override suspend fun setShowClock(show: Boolean) {}
        override suspend fun setShowDeviceIndicators(show: Boolean) {}
        override suspend fun setShowRecentApps(show: Boolean) {}
        override suspend fun setLanguageTag(tag: String) {}
        override suspend fun setSelectedWallpaperId(wallpaperId: String) {
            state.value = state.value.copy(selectedWallpaperId = wallpaperId)
        }
        override suspend fun setShowStartupSplash(show: Boolean) {}
        override suspend fun resetToDefaults() {}
    }

    @Test
    fun getBuiltInWallpapers_returns6ScenicPresets() {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)
        val wallpapers = repo.getBuiltInWallpapers()

        assertEquals(6, wallpapers.size)
        assertTrue(wallpapers.any { it.id == "lunmed_aurora_nordic" })
        assertTrue(wallpapers.any { it.id == "lunmed_cosmic_nebula" })
        assertTrue(wallpapers.any { it.id == "lunmed_sunset_sahara" })
        assertTrue(wallpapers.any { it.id == "lunmed_alpine_mist" })
        assertTrue(wallpapers.any { it.id == "lunmed_tokyo_cyber" })
        assertTrue(wallpapers.any { it.id == "lunmed_obsidian_silk" })
    }

    @Test
    fun getWallpaperById_findsKnownId() {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)

        val wallpaper = repo.getWallpaperById("lunmed_aurora_nordic")
        assertNotNull(wallpaper)
        assertEquals("Nordic Aurora Borealis", wallpaper?.title)
    }

    @Test
    fun selectWallpaper_updatesSelectedStream() = runTest {
        val prefsRepo = TestPreferencesRepository()
        val repo = DefaultWallpaperRepository(prefsRepo)

        val initial = repo.getSelectedWallpaper().first()
        assertEquals("lunmed_aurora_nordic", initial.id)

        repo.selectWallpaper("lunmed_cosmic_nebula")
        val updated = repo.getSelectedWallpaper().first()
        assertEquals("lunmed_cosmic_nebula", updated.id)
        assertEquals("Orion Cosmic Nebula", updated.title)
    }
}

