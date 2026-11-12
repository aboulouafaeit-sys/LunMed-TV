package com.lunmed.tv.feature.device

import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.DeviceDiagnosticsRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.repository.WallpaperRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.DisplayDiagnostics
import com.lunmed.tv.core.model.FullDeviceReport
import com.lunmed.tv.core.model.HardwarePlatformDiagnostics
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.MemoryDiagnostics
import com.lunmed.tv.core.model.NetworkDiagnostics
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.core.model.StorageDiagnostics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceCenterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeReport = FullDeviceReport(
        memory = MemoryDiagnostics(
            totalBytes = 4L * 1024 * 1024 * 1024,
            availableBytes = 2L * 1024 * 1024 * 1024,
            usedBytes = 2L * 1024 * 1024 * 1024,
            usageRatio = 0.5f,
            isLowMemory = false,
            thresholdBytes = 500L * 1024 * 1024,
        ),
        storage = StorageDiagnostics(
            totalBytes = 32L * 1024 * 1024 * 1024,
            availableBytes = 16L * 1024 * 1024 * 1024,
            usedBytes = 16L * 1024 * 1024 * 1024,
            usageRatio = 0.5f,
        ),
        display = DisplayDiagnostics(
            resolutionWidth = 3840,
            resolutionHeight = 2160,
            refreshRateHz = 60.0f,
            densityDpi = 320,
            is4k = true,
            hdrSupportSummary = "HDR10, Dolby Vision",
        ),
        platform = HardwarePlatformDiagnostics(
            brand = "Lunmed",
            manufacturer = "Lunmed Labs",
            model = "Titan TV 4K",
            product = "titan_4k",
            socOrBoard = "Amlogic S905X4",
            supportedAbis = "arm64-v8a",
            androidRelease = "14",
            sdkInt = 34,
            securityPatch = "2026-10-01",
            buildDisplay = "LUNMED-TV-PROD-2026",
        ),
        network = NetworkDiagnostics(
            transportName = "Wi-Fi",
            isConnected = true,
            ipAddress = "192.168.1.150",
            wifiSsid = "Lunmed_5G",
            wifiRssiDbm = -55,
            wifiLinkSpeedMbps = 866,
        ),
    )

    private val sampleWallpapers = listOf(
        LunmedWallpaper(
            id = "lunmed_obsidian_01",
            title = "Obsidian Noir",
            description = "Velvet black obsidian",
            primaryColorArgb = 0xFF050505,
            secondaryColorArgb = 0xFF0E0E10,
            ambientGlowArgb = 0xFFD4AF37,
        ),
        LunmedWallpaper(
            id = "lunmed_cosmic_gold_02",
            title = "Cosmic Amber",
            description = "Celestial obsidian",
            primaryColorArgb = 0xFF0A0704,
            secondaryColorArgb = 0xFF1A1208,
            ambientGlowArgb = 0xFFFFB300,
        ),
    )

    private val selectedWpFlow = MutableStateFlow(sampleWallpapers.first())

    private val fakeDiagnosticsRepo = object : DeviceDiagnosticsRepository {
        var clearCacheCalled = false
        override fun getDeviceReport(): FullDeviceReport = fakeReport
        override suspend fun clearMemoryCache(): Long {
            clearCacheCalled = true
            return 128L * 1024 * 1024 // 128 MB
        }
    }

    private val fakeWallpaperRepo = object : WallpaperRepository {
        override fun getBuiltInWallpapers(): List<LunmedWallpaper> = sampleWallpapers
        override fun getWallpaperById(id: String): LunmedWallpaper? = sampleWallpapers.find { it.id == id }
        override fun getSelectedWallpaper(): Flow<LunmedWallpaper> = selectedWpFlow
        override suspend fun selectWallpaper(id: String) {
            val found = sampleWallpapers.find { it.id == id }
            if (found != null) selectedWpFlow.value = found
        }
    }

    private val fakePrefsRepo = object : PreferencesRepository {
        private val state = MutableStateFlow(LauncherPreferences())
        override val preferences: Flow<LauncherPreferences> = state
        override suspend fun setAccentPreset(preset: AccentPreset) {}
        override suspend fun setCardSize(preset: CardSizePreset) {}
        override suspend fun setDensity(density: InterfaceDensity) {}
        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {}
        override suspend fun setShowClock(show: Boolean) {}
        override suspend fun setShowDeviceIndicators(show: Boolean) {}
        override suspend fun setShowRecentApps(show: Boolean) {}
        override suspend fun setLanguageTag(tag: String) {}
        override suspend fun setSelectedWallpaperId(wallpaperId: String) {}
        override suspend fun resetToDefaults() {}
    }

    private val fakeDefaultLauncherHelper = object : DefaultLauncherHelper {
        override fun getState(): DefaultLauncherState =
            DefaultLauncherState(isDefaultLauncher = true, currentDefaultPackage = "com.lunmed.tv", canRequestRole = false)
        override fun createRequestDefaultLauncherIntent() = null
    }

    private val fakeCapabilityDetector = object : DeviceCapabilityDetector {
        override fun detect(): DeviceCapabilities = DeviceCapabilities(
            sdkInt = 34,
            isLeanbackSupported = true,
            isTelevisionUiMode = true,
            hasTouchscreen = false,
            hasDpad = true,
            isLowRamDevice = false,
            screenWidthPx = 3840,
            screenHeightPx = 2160,
            densityDpi = 320,
        )
    }

    private val fakeNetworkMonitor = object : NetworkStatusMonitor {
        override val networkStatus: Flow<NetworkStatus> =
            MutableStateFlow(NetworkStatus(isConnected = true, transport = NetworkTransport.WIFI))
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initializesWithRealDiagnosticsAndWallpapers() = runTest {
        val viewModel = DeviceCenterViewModel(
            deviceDiagnosticsRepository = fakeDiagnosticsRepo,
            wallpaperRepository = fakeWallpaperRepo,
            preferencesRepository = fakePrefsRepo,
            defaultLauncherHelper = fakeDefaultLauncherHelper,
            capabilityDetector = fakeCapabilityDetector,
            networkStatusMonitor = fakeNetworkMonitor,
        )

        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.report)
        assertEquals("Lunmed", state.report?.platform?.brand)
        assertEquals(2, state.wallpapers.size)
        assertEquals("lunmed_obsidian_01", state.selectedWallpaper?.id)
        assertEquals(DeviceCenterTab.RESOURCES, state.activeTab)
        job.cancel()
    }

    @Test
    fun onSelectTab_changesActiveTab() = runTest {
        val viewModel = DeviceCenterViewModel(
            deviceDiagnosticsRepository = fakeDiagnosticsRepo,
            wallpaperRepository = fakeWallpaperRepo,
            preferencesRepository = fakePrefsRepo,
            defaultLauncherHelper = fakeDefaultLauncherHelper,
            capabilityDetector = fakeCapabilityDetector,
            networkStatusMonitor = fakeNetworkMonitor,
        )

        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()

        viewModel.onSelectTab(DeviceCenterTab.WALLPAPERS)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(DeviceCenterTab.WALLPAPERS, state.activeTab)
        job.cancel()
    }

    @Test
    fun onSelectWallpaper_updatesActiveWallpaperAndShowsNotification() = runTest {
        val viewModel = DeviceCenterViewModel(
            deviceDiagnosticsRepository = fakeDiagnosticsRepo,
            wallpaperRepository = fakeWallpaperRepo,
            preferencesRepository = fakePrefsRepo,
            defaultLauncherHelper = fakeDefaultLauncherHelper,
            capabilityDetector = fakeCapabilityDetector,
            networkStatusMonitor = fakeNetworkMonitor,
        )

        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()

        viewModel.onSelectWallpaper("lunmed_cosmic_gold_02")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("lunmed_cosmic_gold_02", state.selectedWallpaper?.id)
        assertTrue(state.statusNotification?.contains("Cosmic Amber") == true)
        job.cancel()
    }

    @Test
    fun onClearMemoryCache_callsRepositoryAndSetsFreedMbMessage() = runTest {
        val viewModel = DeviceCenterViewModel(
            deviceDiagnosticsRepository = fakeDiagnosticsRepo,
            wallpaperRepository = fakeWallpaperRepo,
            preferencesRepository = fakePrefsRepo,
            defaultLauncherHelper = fakeDefaultLauncherHelper,
            capabilityDetector = fakeCapabilityDetector,
            networkStatusMonitor = fakeNetworkMonitor,
        )

        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        advanceUntilIdle()

        viewModel.onClearMemoryCache()
        advanceUntilIdle()

        assertTrue(fakeDiagnosticsRepo.clearCacheCalled)
        val state = viewModel.uiState.value
        assertTrue(state.statusNotification?.contains("Optimized") == true)
        job.cancel()
    }
}

