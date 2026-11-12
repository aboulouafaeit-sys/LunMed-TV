package com.lunmed.tv.feature.settings

import android.content.Intent
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.feature.settings.model.SettingsSection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SettingsViewModelTest {

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
    fun `initial state loads preferences capabilities and default launcher state`() = runTest(dispatcher) {
        val prefsRepo = FakePreferencesRepository()
        val helper = FakeDefaultLauncherHelper()
        val vm = SettingsViewModel(
            preferencesRepository = prefsRepo,
            defaultLauncherHelper = helper,
            capabilityDetector = FakeCapabilityDetector(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        assertEquals(SettingsSection.APPEARANCE, vm.uiState.value.selectedSection)
        assertEquals(AccentPreset.CHAMPAGNE_GOLD, vm.uiState.value.preferences.accentPreset)
        assertTrue(vm.uiState.value.defaultLauncherState.isDefaultLauncher)
        assertFalse(vm.uiState.value.isResetConfirmationOpen)

        job.cancel()
    }

    @Test
    fun `section switching updates selectedSection`() = runTest(dispatcher) {
        val vm = SettingsViewModel(
            preferencesRepository = FakePreferencesRepository(),
            defaultLauncherHelper = FakeDefaultLauncherHelper(),
            capabilityDetector = FakeCapabilityDetector(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        vm.onSelectSection(SettingsSection.ANIMATIONS)
        assertEquals(SettingsSection.ANIMATIONS, vm.uiState.value.selectedSection)

        vm.onSelectSection(SettingsSection.ABOUT)
        assertEquals(SettingsSection.ABOUT, vm.uiState.value.selectedSection)

        job.cancel()
    }

    @Test
    fun `preference mutations update preferences in repository and state`() = runTest(dispatcher) {
        val prefsRepo = FakePreferencesRepository()
        val vm = SettingsViewModel(
            preferencesRepository = prefsRepo,
            defaultLauncherHelper = FakeDefaultLauncherHelper(),
            capabilityDetector = FakeCapabilityDetector(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        vm.onSetAccentPreset(AccentPreset.ICE_CYAN)
        assertEquals(AccentPreset.ICE_CYAN, vm.uiState.value.preferences.accentPreset)

        vm.onSetDensity(InterfaceDensity.COMPACT)
        assertEquals(InterfaceDensity.COMPACT, vm.uiState.value.preferences.density)

        vm.onSetCardSize(CardSizePreset.CINEMA)
        assertEquals(CardSizePreset.CINEMA, vm.uiState.value.preferences.cardSize)

        vm.onSetAnimationIntensity(AnimationIntensity.MINIMAL)
        assertEquals(AnimationIntensity.MINIMAL, vm.uiState.value.preferences.animationIntensity)

        vm.onToggleShowClock(false)
        assertFalse(vm.uiState.value.preferences.showClock)

        vm.onToggleShowDeviceIndicators(false)
        assertFalse(vm.uiState.value.preferences.showDeviceIndicators)

        vm.onToggleShowRecentApps(false)
        assertFalse(vm.uiState.value.preferences.showRecentApps)

        vm.onSetLanguageTag("ar")
        assertEquals("ar", vm.uiState.value.preferences.languageTag)
        assertNotNull(vm.uiState.value.statusMessage)

        job.cancel()
    }

    @Test
    fun `reset to defaults flow shows dialog confirms reset and clears status message`() = runTest(dispatcher) {
        val prefsRepo = FakePreferencesRepository()
        val vm = SettingsViewModel(
            preferencesRepository = prefsRepo,
            defaultLauncherHelper = FakeDefaultLauncherHelper(),
            capabilityDetector = FakeCapabilityDetector(),
            networkStatusMonitor = FakeNetworkStatusMonitor(),
        )

        val job = backgroundScope.launch(dispatcher) { vm.uiState.collect() }

        // Mutate first
        vm.onSetAccentPreset(AccentPreset.OBSIDIAN_AMBER)
        assertEquals(AccentPreset.OBSIDIAN_AMBER, vm.uiState.value.preferences.accentPreset)

        // Open reset dialog
        vm.onOpenResetConfirmation()
        assertTrue(vm.uiState.value.isResetConfirmationOpen)

        // Dismiss first
        vm.onDismissResetConfirmation()
        assertFalse(vm.uiState.value.isResetConfirmationOpen)

        // Re-open and confirm
        vm.onOpenResetConfirmation()
        vm.onConfirmResetToDefaults()
        assertFalse(vm.uiState.value.isResetConfirmationOpen)
        assertEquals(AccentPreset.CHAMPAGNE_GOLD, vm.uiState.value.preferences.accentPreset)
        assertNotNull(vm.uiState.value.statusMessage)

        vm.onClearStatusMessage()
        assertNull(vm.uiState.value.statusMessage)

        job.cancel()
    }

    private class FakePreferencesRepository : PreferencesRepository {
        private val state = MutableStateFlow(LauncherPreferences())
        override val preferences = state

        override suspend fun setAccentPreset(preset: AccentPreset) {
            state.value = state.value.copy(accentPreset = preset)
        }

        override suspend fun setCardSize(preset: CardSizePreset) {
            state.value = state.value.copy(cardSize = preset)
        }

        override suspend fun setDensity(density: InterfaceDensity) {
            state.value = state.value.copy(density = density)
        }

        override suspend fun setAnimationIntensity(intensity: AnimationIntensity) {
            state.value = state.value.copy(animationIntensity = intensity)
        }

        override suspend fun setShowClock(show: Boolean) {
            state.value = state.value.copy(showClock = show)
        }

        override suspend fun setShowDeviceIndicators(show: Boolean) {
            state.value = state.value.copy(showDeviceIndicators = show)
        }

        override suspend fun setShowRecentApps(show: Boolean) {
            state.value = state.value.copy(showRecentApps = show)
        }

        override suspend fun setLanguageTag(tag: String) {
            state.value = state.value.copy(languageTag = tag)
        }

        override suspend fun setSelectedWallpaperId(wallpaperId: String) {
            state.value = state.value.copy(selectedWallpaperId = wallpaperId)
        }

        override suspend fun resetToDefaults() {
            state.value = LauncherPreferences()
        }
    }

    private class FakeDefaultLauncherHelper : DefaultLauncherHelper {
        private var isDefault = true
        override fun getState() = DefaultLauncherState(isDefault, "com.lunmed.tv", false)
        override fun createRequestDefaultLauncherIntent(): Intent? = null
    }

    private class FakeCapabilityDetector : DeviceCapabilityDetector {
        override fun detect() = DeviceCapabilities(
            sdkInt = 35,
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

    private class FakeNetworkStatusMonitor : NetworkStatusMonitor {
        override val networkStatus = MutableStateFlow(NetworkStatus(true, NetworkTransport.ETHERNET))
    }
}

