package com.lunmed.tv.feature.home

import android.content.Intent
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LaunchResult
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `buildHomeSections separates favorites, recent apps, and categories`() {
        val apps = listOf(
            app("com.tv.one", "Netflix", categoryId = "media", isFavorite = true, lastLaunched = 100L),
            app("com.tv.two", "YouTube", categoryId = "media", isFavorite = false, lastLaunched = 500L),
            app("com.game.one", "Asphalt", categoryId = "games", isFavorite = false, lastLaunched = 0L),
        )

        val sections = HomeViewModel.buildHomeSections(apps, LauncherPreferences(showRecentApps = true))
        assertEquals(listOf("com.tv.one"), sections.favorites.map { it.packageName })
        assertEquals(listOf("com.tv.two", "com.tv.one"), sections.recentApps.map { it.packageName })
        assertEquals(listOf("media", "games"), sections.categories.map { it.id })

        val withoutRecent = HomeViewModel.buildHomeSections(apps, LauncherPreferences(showRecentApps = false))
        assertTrue(withoutRecent.recentApps.isEmpty())
    }

    @Test
    fun `HomeViewModel tracks focus, launch errors, and context actions`() = runTest(dispatcher) {
        val repo = FakeAppRepository(
            listOf(
                app("com.ok", "OK App", isFavorite = false),
                app("com.broken", "Broken App", isFavorite = true),
            ),
        )
        val vm = HomeViewModel(
            appRepository = repo,
            preferencesRepository = FakePreferencesRepository(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
            defaultLauncherHelper = FakeDefaultLauncherHelper(),
            capabilityDetector = FakeCapabilityDetector(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        assertEquals(2, vm.uiState.value.allAppsCount)
        assertEquals("com.broken", vm.uiState.value.focusedApp?.packageName)

        vm.onAppFocused(repo.currentApps.first { it.packageName == "com.ok" })
        assertEquals("com.ok", vm.uiState.value.focusedApp?.packageName)

        vm.onAppClicked(repo.currentApps.first { it.packageName == "com.broken" })
        assertNotNull(vm.uiState.value.statusMessage)
        vm.onClearStatusMessage()
        assertNull(vm.uiState.value.statusMessage)

        vm.onToggleFavorite(repo.currentApps.first { it.packageName == "com.ok" })
        assertTrue(vm.uiState.value.favorites.any { it.packageName == "com.ok" })

        vm.onHideApp(repo.currentApps.first { it.packageName == "com.broken" })
        assertEquals(1, vm.uiState.value.allAppsCount)

        job.cancel()
    }

    @Test
    fun `HomeViewModel prompts accessibility setup on startup when unprotected and auto-completes on watchdog enable`() =
        runTest(dispatcher) {
            val prefsRepo = FakePreferencesRepository()
            val launcherHelper = FakeDefaultLauncherHelper(
                DefaultLauncherState(
                    isDefaultLauncher = false,
                    currentDefaultPackage = "com.google.android.apps.tv.launcherx",
                    canRequestRole = false,
                    isWatchdogEnabled = false,
                ),
            )
            val vm = HomeViewModel(
                appRepository = FakeAppRepository(listOf(app("com.ok", "OK App"))),
                preferencesRepository = prefsRepo,
                networkStatusMonitor = FakeNetworkStatusMonitor(),
                defaultLauncherHelper = launcherHelper,
                capabilityDetector = FakeCapabilityDetector(),
            )

            val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

            assertTrue(vm.uiState.value.showLauncherSetupDialog)
            assertTrue(vm.uiState.value.isLauncherSetupStartupPrompt)

            // User enables LUNMED TV in Accessibility settings and returns to Home
            launcherHelper.launcherState = launcherHelper.launcherState.copy(isWatchdogEnabled = true)
            vm.onRefreshLauncherState()

            assertFalse(vm.uiState.value.showLauncherSetupDialog)
            assertTrue(vm.uiState.value.defaultLauncherState.isEffectivelyDefault)
            assertEquals(HomeStatusMessage.LauncherActivated, vm.uiState.value.statusMessage)

            job.cancel()
        }

    @Test
    fun `HomeViewModel suppresses startup setup prompt when user chooses Dont Ask Again`() =
        runTest(dispatcher) {
            val prefsRepo = FakePreferencesRepository()
            val launcherHelper = FakeDefaultLauncherHelper(
                DefaultLauncherState(
                    isDefaultLauncher = false,
                    currentDefaultPackage = "com.android.launcher3",
                    canRequestRole = true,
                    isWatchdogEnabled = false,
                ),
            )
            val vm = HomeViewModel(
                appRepository = FakeAppRepository(listOf(app("com.ok", "OK App"))),
                preferencesRepository = prefsRepo,
                networkStatusMonitor = FakeNetworkStatusMonitor(),
                defaultLauncherHelper = launcherHelper,
                capabilityDetector = FakeCapabilityDetector(),
            )

            val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

            assertTrue(vm.uiState.value.showLauncherSetupDialog)
            vm.onDisableStartupLauncherPrompt()

            assertFalse(vm.uiState.value.showLauncherSetupDialog)
            assertFalse(prefsRepo.preferences.value.promptDefaultLauncherOnStartup)

            job.cancel()
        }

    private fun app(
        pkg: String,
        label: String,
        categoryId: String = "media",
        isFavorite: Boolean = false,
        lastLaunched: Long = 0L,
    ) = AppInfo(
        packageName = pkg,
        activityName = "$pkg.Main",
        label = label,
        categoryId = categoryId,
        isFavorite = isFavorite,
        lastLaunchedEpochMs = lastLaunched,
    )

    private class FakeAppRepository(initial: List<AppInfo>) : AppRepository {
        private val apps = MutableStateFlow(initial)
        val currentApps: List<AppInfo> get() = apps.value

        override fun observeApps(includeHidden: Boolean): Flow<List<AppInfo>> = apps
        override suspend fun refresh() = Unit
        override suspend fun launchApp(packageName: String): LaunchResult =
            if (packageName == "com.ok") LaunchResult.Success else LaunchResult.PackageUnavailable(packageName)

        override suspend fun toggleFavorite(packageName: String) {
            apps.value = apps.value.map {
                if (it.packageName == packageName) it.copy(isFavorite = !it.isFavorite) else it
            }
        }

        override suspend fun setHidden(packageName: String, hidden: Boolean) {
            apps.value = apps.value.filterNot { it.packageName == packageName }
        }

        override suspend fun updateCustomOrder(orderedPackages: List<String>) = Unit
        override suspend fun setCategory(packageName: String, categoryId: String?) = Unit
    }

    private class FakePreferencesRepository : PreferencesRepository {
        override val preferences = MutableStateFlow(LauncherPreferences())
        override suspend fun setAccentPreset(preset: AccentPreset) = Unit
        override suspend fun setCardSize(preset: CardSizePreset) = Unit
        override suspend fun setIconDisplayMode(mode: com.lunmed.tv.core.model.IconDisplayMode) = Unit
        override suspend fun setDensity(density: InterfaceDensity) = Unit
        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) = Unit
        override suspend fun setShowClock(show: Boolean) = Unit
        override suspend fun setShowDeviceIndicators(show: Boolean) = Unit
        override suspend fun setShowRecentApps(show: Boolean) = Unit
        override suspend fun setLanguageTag(tag: String) = Unit
        override suspend fun setSelectedWallpaperId(wallpaperId: String) = Unit
        override suspend fun setShowStartupSplash(show: Boolean) = Unit
        override suspend fun setPromptDefaultLauncherOnStartup(prompt: Boolean) {
            preferences.value = preferences.value.copy(promptDefaultLauncherOnStartup = prompt)
        }
        override suspend fun resetToDefaults() = Unit
    }

    private class FakeNetworkStatusMonitor : NetworkStatusMonitor {
        override val networkStatus = MutableStateFlow(NetworkStatus(true, NetworkTransport.ETHERNET))
    }

    private class FakeDefaultLauncherHelper(
        var launcherState: DefaultLauncherState = DefaultLauncherState(true, "com.lunmed.tv", false),
    ) : DefaultLauncherHelper {
        override fun getState() = launcherState
        override fun createRequestDefaultLauncherIntent(): Intent? = null
    }

    private class FakeCapabilityDetector : DeviceCapabilityDetector {
        override fun detect() = DeviceCapabilities(
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
}

