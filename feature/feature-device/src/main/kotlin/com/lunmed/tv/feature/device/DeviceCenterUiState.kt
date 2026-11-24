package com.lunmed.tv.feature.device

import androidx.annotation.StringRes
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.FullDeviceReport
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.ui.R

enum class DeviceCenterTab(@StringRes val labelRes: Int) {
    RESOURCES(R.string.device_tab_resources),
    WALLPAPERS(R.string.device_tab_wallpapers),
    HARDWARE(R.string.device_tab_hardware),
}

/**
 * Locale-independent description of a transient status message.
 *
 * The view model never builds user-visible prose: it emits a semantic message and the
 * composable resolves it through [androidx.compose.ui.res.stringResource], so the toast
 * always follows the active configuration locale (including live RTL / language switches).
 */
sealed interface DeviceStatusMessage {
    data object TelemetryRefreshed : DeviceStatusMessage

    /** [freedMbFormatted] is null when no meaningful cache was reclaimed. */
    data class CacheOptimized(val freedMbFormatted: String?) : DeviceStatusMessage

    data class WallpaperApplied(val wallpaperTitle: String) : DeviceStatusMessage
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
    val statusNotification: DeviceStatusMessage? = null,
)

