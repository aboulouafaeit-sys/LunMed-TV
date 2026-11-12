package com.lunmed.tv.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.feature.settings.model.SettingsSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val defaultLauncherHelper: DefaultLauncherHelper,
    capabilityDetector: DeviceCapabilityDetector,
    networkStatusMonitor: NetworkStatusMonitor,
) : ViewModel() {

    private val capabilities = capabilityDetector.detect()
    private val selectedSection = MutableStateFlow(SettingsSection.APPEARANCE)
    private val isResetConfirmationOpen = MutableStateFlow(false)
    private val statusMessage = MutableStateFlow<String?>(null)
    private val defaultLauncherState = MutableStateFlow(defaultLauncherHelper.getState())

    private val localSettingsState = combine(
        selectedSection,
        isResetConfirmationOpen,
        statusMessage,
        defaultLauncherState,
    ) { section, resetOpen, msg, launcherState ->
        LocalSettingsState(section, resetOpen, msg, launcherState)
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.preferences,
        networkStatusMonitor.networkStatus,
        localSettingsState,
    ) { prefs, network, local ->
        SettingsUiState(
            isLoading = false,
            preferences = prefs,
            capabilities = capabilities,
            networkStatus = network,
            defaultLauncherState = local.launcherState,
            selectedSection = local.section,
            isResetConfirmationOpen = local.resetConfirmationOpen,
            statusMessage = local.message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(capabilities = capabilities),
    )

    fun onSelectSection(section: SettingsSection) {
        selectedSection.value = section
    }

    fun onSetAccentPreset(preset: AccentPreset) {
        viewModelScope.launch {
            preferencesRepository.setAccentPreset(preset)
        }
    }

    fun onSetDensity(density: InterfaceDensity) {
        viewModelScope.launch {
            preferencesRepository.setDensity(density)
        }
    }

    fun onSetCardSize(size: CardSizePreset) {
        viewModelScope.launch {
            preferencesRepository.setCardSize(size)
        }
    }

    fun onSetAnimationIntensity(intensity: AnimationIntensity) {
        viewModelScope.launch {
            preferencesRepository.setAnimationIntensity(intensity)
        }
    }

    fun onToggleShowClock(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowClock(show)
        }
    }

    fun onToggleShowDeviceIndicators(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowDeviceIndicators(show)
        }
    }

    fun onToggleShowRecentApps(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowRecentApps(show)
        }
    }

    fun onSetLanguageTag(tag: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguageTag(tag)
            statusMessage.value = "Language preference updated"
        }
    }

    fun onOpenResetConfirmation() {
        isResetConfirmationOpen.value = true
    }

    fun onDismissResetConfirmation() {
        isResetConfirmationOpen.value = false
    }

    fun onConfirmResetToDefaults() {
        viewModelScope.launch {
            preferencesRepository.resetToDefaults()
            isResetConfirmationOpen.value = false
            statusMessage.value = "Settings restored to defaults"
        }
    }

    fun onRefreshLauncherState() {
        defaultLauncherState.value = defaultLauncherHelper.getState()
    }

    fun onClearStatusMessage() {
        statusMessage.value = null
    }

    private data class LocalSettingsState(
        val section: SettingsSection,
        val resetConfirmationOpen: Boolean,
        val message: String?,
        val launcherState: com.lunmed.tv.core.model.DefaultLauncherState,
    )
}

