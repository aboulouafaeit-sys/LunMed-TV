package com.lunmed.tv.feature.settings

import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.feature.settings.model.SettingsSection

data class SettingsUiState(
    val isLoading: Boolean = false,
    val preferences: LauncherPreferences = LauncherPreferences(),
    val capabilities: DeviceCapabilities? = null,
    val networkStatus: NetworkStatus = NetworkStatus(false, NetworkTransport.OFFLINE),
    val defaultLauncherState: DefaultLauncherState = DefaultLauncherState(
        isDefaultLauncher = false,
        currentDefaultPackage = null,
        canRequestRole = false,
    ),
    val selectedSection: SettingsSection = SettingsSection.APPEARANCE,
    val isResetConfirmationOpen: Boolean = false,
    val statusMessage: String? = null,
)

