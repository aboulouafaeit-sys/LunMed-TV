package com.lunmed.tv.feature.device

import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.FullDeviceReport
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.NetworkStatus

enum class DeviceCenterTab(val label: String) {
    RESOURCES("Performance & Memory"),
    WALLPAPERS("Cinematic Wallpapers"),
    HARDWARE("System Architecture"),
}

data class DeviceCenterUiState(
    val activeTab: DeviceCenterTab = DeviceCenterTab.RESOURCES,
    val report: FullDeviceReport? = null,
    val wallpapers: List<LunmedWallpaper> = emptyList(),
    val selectedWallpaper: LunmedWallpaper? = null,
    val preferences: LauncherPreferences = LauncherPreferences(),
    val networkStatus: NetworkStatus = NetworkStatus(isConnected = true, transport = com.lunmed.tv.core.model.NetworkTransport.OTHER),
    val capabilities: DeviceCapabilities? = null,
    val defaultLauncherState: DefaultLauncherState = DefaultLauncherState(isDefaultLauncher = false, currentDefaultPackage = null, canRequestRole = false),
    val isOptimizing: Boolean = false,
    val statusNotification: String? = null,
)

