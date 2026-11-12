package com.lunmed.tv.feature.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.LaunchResult
import com.lunmed.tv.feature.apps.model.AppLibraryFilterEngine
import com.lunmed.tv.feature.apps.model.AppSortOrder
import com.lunmed.tv.feature.apps.model.CategoryFilterItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppLibraryViewModel @Inject constructor(
    private val appRepository: AppRepository,
    preferencesRepository: PreferencesRepository,
    networkStatusMonitor: NetworkStatusMonitor,
    capabilityDetector: DeviceCapabilityDetector,
) : ViewModel() {

    private val capabilities = capabilityDetector.detect()

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(AppLibraryFilterEngine.CATEGORY_ALL)
    private val sortOrder = MutableStateFlow(AppSortOrder.NAME_ASC)
    private val focusedApp = MutableStateFlow<AppInfo?>(null)
    private val contextMenuApp = MutableStateFlow<AppInfo?>(null)
    private val categoryPickerApp = MutableStateFlow<AppInfo?>(null)
    private val statusMessage = MutableStateFlow<String?>(null)

    private val filterState = combine(
        searchQuery,
        selectedCategory,
        sortOrder,
    ) { query, cat, sort -> FilterState(query, cat, sort) }

    private val overlayState = combine(
        focusedApp,
        contextMenuApp,
        categoryPickerApp,
        statusMessage,
    ) { focused, contextMenu, catPicker, msg ->
        OverlayState(focused, contextMenu, catPicker, msg)
    }

    val uiState: StateFlow<AppLibraryUiState> = combine(
        appRepository.observeApps(includeHidden = true),
        preferencesRepository.preferences,
        networkStatusMonitor.networkStatus,
        filterState,
        overlayState,
    ) { allApps, prefs, network, filter, overlay ->
        val counts = AppLibraryFilterEngine.computeCategoryCounts(allApps)

        val categoriesWithCounts = AppLibraryFilterEngine.DEFAULT_CATEGORIES.mapNotNull { (id, title) ->
            val count = counts[id] ?: 0
            if (id == AppLibraryFilterEngine.CATEGORY_HIDDEN && count == 0) {
                null
            } else {
                CategoryFilterItem(id = id, title = title, count = count)
            }
        }

        val filteredApps = AppLibraryFilterEngine.filterAndSort(
            allApps = allApps,
            searchQuery = filter.query,
            selectedCategory = filter.category,
            sortOrder = filter.sortOrder,
        )

        val effectiveFocus = overlay.focused?.let { current ->
            filteredApps.firstOrNull { it.packageName == current.packageName }
        } ?: filteredApps.firstOrNull()

        AppLibraryUiState(
            isLoading = false,
            apps = filteredApps,
            totalAppsCount = allApps.size,
            visibleAppsCount = filteredApps.size,
            searchQuery = filter.query,
            selectedCategory = filter.category,
            availableCategories = categoriesWithCounts,
            sortOrder = filter.sortOrder,
            focusedApp = effectiveFocus,
            contextMenuApp = overlay.contextMenu,
            categoryPickerApp = overlay.categoryPicker,
            cardSize = prefs.cardSize,
            selectedWallpaperId = prefs.selectedWallpaperId,
            networkStatus = network,
            capabilities = capabilities,
            statusMessage = overlay.message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppLibraryUiState(capabilities = capabilities),
    )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onCategorySelected(categoryId: String) {
        selectedCategory.value = categoryId
    }

    fun onSortOrderChanged(order: AppSortOrder) {
        sortOrder.value = order
    }

    fun onAppFocused(app: AppInfo) {
        focusedApp.value = app
    }

    fun onAppClicked(app: AppInfo) {
        // Prevent launching app if context menu or category picker dialog is active
        if (contextMenuApp.value != null || categoryPickerApp.value != null) return
        viewModelScope.launch {
            when (val result = appRepository.launchApp(app.packageName)) {
                LaunchResult.Success -> statusMessage.value = null
                is LaunchResult.PackageUnavailable ->
                    statusMessage.value = "${app.label} is unavailable or was uninstalled."
                is LaunchResult.PermissionDenied ->
                    statusMessage.value = "Permission denied while launching ${app.label}."
                is LaunchResult.Failed ->
                    statusMessage.value = "Could not open ${app.label}: ${result.reason}"
            }
        }
    }

    fun onAppLongClicked(app: AppInfo) {
        contextMenuApp.value = app
    }

    fun onDismissContextMenu() {
        contextMenuApp.value = null
    }

    fun onToggleFavorite(app: AppInfo) {
        viewModelScope.launch {
            appRepository.toggleFavorite(app.packageName)
            contextMenuApp.value = null
        }
    }

    fun onToggleHidden(app: AppInfo) {
        viewModelScope.launch {
            appRepository.setHidden(app.packageName, hidden = !app.isHidden)
            contextMenuApp.value = null
        }
    }

    fun onOpenCategoryPicker(app: AppInfo) {
        categoryPickerApp.value = app
        contextMenuApp.value = null
    }

    fun onDismissCategoryPicker() {
        categoryPickerApp.value = null
    }

    fun onAssignCategory(app: AppInfo, categoryId: String?) {
        viewModelScope.launch {
            appRepository.setCategory(app.packageName, categoryId)
            categoryPickerApp.value = null
        }
    }

    fun onClearStatusMessage() {
        statusMessage.value = null
    }

    private data class FilterState(
        val query: String,
        val category: String,
        val sortOrder: AppSortOrder,
    )

    private data class OverlayState(
        val focused: AppInfo?,
        val contextMenu: AppInfo?,
        val categoryPicker: AppInfo?,
        val message: String?,
    )
}

