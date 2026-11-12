package com.lunmed.tv.feature.apps

import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.feature.apps.model.AppSortOrder
import com.lunmed.tv.feature.apps.model.CategoryFilterItem

data class AppLibraryUiState(
    val isLoading: Boolean = true,
    val apps: List<AppInfo> = emptyList(),
    val totalAppsCount: Int = 0,
    val visibleAppsCount: Int = 0,
    val searchQuery: String = "",
    val selectedCategory: String = "all",
    val availableCategories: List<CategoryFilterItem> = emptyList(),
    val sortOrder: AppSortOrder = AppSortOrder.NAME_ASC,
    val focusedApp: AppInfo? = null,
    val contextMenuApp: AppInfo? = null,
    val categoryPickerApp: AppInfo? = null,
    val cardSize: CardSizePreset = CardSizePreset.STANDARD,
    val iconDisplayMode: com.lunmed.tv.core.model.IconDisplayMode = com.lunmed.tv.core.model.IconDisplayMode.CINEMA_BANNER,
    val selectedWallpaperId: String = "lunmed_aurora_nordic",
    val networkStatus: NetworkStatus = NetworkStatus(false, NetworkTransport.OFFLINE),
    val capabilities: DeviceCapabilities? = null,
    val statusMessage: String? = null,
)

