package com.lunmed.tv.feature.home

import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport

data class HomeCategorySection(
    val id: String,
    val title: String,
    val apps: List<AppInfo>,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val favorites: List<AppInfo> = emptyList(),
    val recentApps: List<AppInfo> = emptyList(),
    val categories: List<HomeCategorySection> = emptyList(),
    val allAppsCount: Int = 0,
    val focusedApp: AppInfo? = null,
    val contextMenuApp: AppInfo? = null,
    val preferences: LauncherPreferences = LauncherPreferences(),
    val networkStatus: NetworkStatus = NetworkStatus(false, NetworkTransport.OFFLINE),
    val defaultLauncherState: DefaultLauncherState = DefaultLauncherState(
        isDefaultLauncher = false,
        currentDefaultPackage = null,
        canRequestRole = false,
    ),
    val capabilities: DeviceCapabilities? = null,
    val statusMessage: String? = null,
)

