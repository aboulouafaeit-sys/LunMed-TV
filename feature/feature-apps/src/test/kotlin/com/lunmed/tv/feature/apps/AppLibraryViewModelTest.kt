package com.lunmed.tv.feature.apps

import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.LaunchResult
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.feature.apps.model.AppSortOrder
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
class AppLibraryViewModelTest {

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
    fun `viewModel filters by search and category reactively`() = runTest(dispatcher) {
        val repo = FakeAppRepository(
            listOf(
                app("com.plex", "Plex", categoryId = "media", isFavorite = true),
                app("com.kodi", "Kodi", categoryId = "media", isFavorite = false),
                app("com.retroarch", "RetroArch", categoryId = "games", isFavorite = false),
                app("com.hidden", "Hidden Diagnostic", categoryId = "system", isHidden = true),
            ),
        )

        val vm = AppLibraryViewModel(
            appRepository = repo,
            preferencesRepository = FakePreferencesRepository(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
            capabilityDetector = FakeCapabilityDetector(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        // Initial: all visible apps (3 apps, excluding hidden)
        assertEquals(3, vm.uiState.value.visibleAppsCount)
        assertEquals(4, vm.uiState.value.totalAppsCount)

        // Filter by category: games
        vm.onCategorySelected("games")
        assertEquals(1, vm.uiState.value.visibleAppsCount)
        assertEquals("com.retroarch", vm.uiState.value.apps.single().packageName)

        // Filter by category: hidden
        vm.onCategorySelected("hidden")
        assertEquals(1, vm.uiState.value.visibleAppsCount)
        assertEquals("com.hidden", vm.uiState.value.apps.single().packageName)

        // Search query
        vm.onCategorySelected("all")
        vm.onSearchQueryChanged("kodi")
        assertEquals(1, vm.uiState.value.visibleAppsCount)
        assertEquals("com.kodi", vm.uiState.value.apps.single().packageName)

        // Sort order change
        vm.onSearchQueryChanged("")
        vm.onSortOrderChanged(AppSortOrder.NAME_DESC)
        assertEquals(listOf("RetroArch", "Plex", "Kodi"), vm.uiState.value.apps.map { it.label })

        job.cancel()
    }

    @Test
    fun `viewModel actions handle favorites, hiding, category assignment, and launch errors`() = runTest(dispatcher) {
        val repo = FakeAppRepository(
            listOf(
                app("com.app.ok", "App OK", categoryId = "other", isFavorite = false, isHidden = false),
                app("com.app.broken", "App Broken", categoryId = "other", isFavorite = false, isHidden = false),
            ),
        )

        val vm = AppLibraryViewModel(
            appRepository = repo,
            preferencesRepository = FakePreferencesRepository(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
            capabilityDetector = FakeCapabilityDetector(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        val okApp = repo.currentApps.first { it.packageName == "com.app.ok" }

        // Toggle Favorite
        vm.onToggleFavorite(okApp)
        assertTrue(repo.currentApps.first { it.packageName == "com.app.ok" }.isFavorite)

        // Toggle Hidden
        vm.onToggleHidden(okApp)
        assertTrue(repo.currentApps.first { it.packageName == "com.app.ok" }.isHidden)

        // Assign Category
        vm.onAssignCategory(okApp, "media")
        assertEquals("media", repo.currentApps.first { it.packageName == "com.app.ok" }.categoryId)

        // Launch broken app triggers statusMessage
        val brokenApp = repo.currentApps.first { it.packageName == "com.app.broken" }
        vm.onAppClicked(brokenApp)
        assertNotNull(vm.uiState.value.statusMessage)

        vm.onClearStatusMessage()
        assertNull(vm.uiState.value.statusMessage)

        job.cancel()
    }

    private fun app(
        pkg: String,
        label: String,
        categoryId: String = "other",
        isFavorite: Boolean = false,
        isHidden: Boolean = false,
    ) = AppInfo(
        packageName = pkg,
        activityName = "$pkg.Main",
        label = label,
        categoryId = categoryId,
        isFavorite = isFavorite,
        isHidden = isHidden,
    )

    private class FakeAppRepository(initial: List<AppInfo>) : AppRepository {
        private val apps = MutableStateFlow(initial)
        val currentApps: List<AppInfo> get() = apps.value

        override fun observeApps(includeHidden: Boolean): Flow<List<AppInfo>> = apps
        override suspend fun refresh() = Unit
        override suspend fun launchApp(packageName: String): LaunchResult =
            if (packageName == "com.app.ok") LaunchResult.Success else LaunchResult.PackageUnavailable(packageName)

        override suspend fun toggleFavorite(packageName: String) {
            apps.value = apps.value.map {
                if (it.packageName == packageName) it.copy(isFavorite = !it.isFavorite) else it
            }
        }

        override suspend fun setHidden(packageName: String, hidden: Boolean) {
            apps.value = apps.value.map {
                if (it.packageName == packageName) it.copy(isHidden = hidden) else it
            }
        }

        override suspend fun updateCustomOrder(orderedPackages: List<String>) = Unit

        override suspend fun setCategory(packageName: String, categoryId: String?) {
            apps.value = apps.value.map {
                if (it.packageName == packageName) it.copy(categoryId = categoryId) else it
            }
        }
    }

    private class FakePreferencesRepository : PreferencesRepository {
        override val preferences = MutableStateFlow(LauncherPreferences())
        override suspend fun setAccentPreset(preset: AccentPreset) = Unit
        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) = Unit
        override suspend fun resetToDefaults() = Unit
    }

    private class FakeNetworkStatusMonitor : NetworkStatusMonitor {
        override val networkStatus = MutableStateFlow(NetworkStatus(true, NetworkTransport.ETHERNET))
    }

    private class FakeCapabilityDetector : DeviceCapabilityDetector {
        override fun detect() = DeviceCapabilities(
            sdkInt = 34,
            isLeanbackSupported = true,
            isTelevisionUiMode = true,
            hasTouchscreen = false,
            hasDpad = true,
            isLowRamDevice = false,
            screenWidthPx = 1920,
            screenHeightPx = 1080,
            densityDpi = 320,
        )
    }
}

