package com.lunmed.tv.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.LaunchResult
import com.lunmed.tv.core.model.LauncherPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository,
    preferencesRepository: PreferencesRepository,
    networkStatusMonitor: NetworkStatusMonitor,
    private val defaultLauncherHelper: DefaultLauncherHelper,
    capabilityDetector: DeviceCapabilityDetector,
) : ViewModel() {

    private val capabilities = capabilityDetector.detect()
    private val focusedApp = MutableStateFlow<AppInfo?>(null)
    private val contextMenuApp = MutableStateFlow<AppInfo?>(null)
    private val statusMessage = MutableStateFlow<String?>(null)
    private val defaultLauncherState = MutableStateFlow(defaultLauncherHelper.getState())

    private val localOverlayState = combine(
        focusedApp,
        contextMenuApp,
        statusMessage,
        defaultLauncherState,
    ) { focused, menuApp, message, launcherState ->
        LocalOverlayState(focused, menuApp, message, launcherState)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        appRepository.observeApps(includeHidden = false),
        preferencesRepository.preferences,
        networkStatusMonitor.networkStatus,
        localOverlayState,
    ) { apps, prefs, network, overlay ->
        val sections = buildHomeSections(apps, prefs)
        val effectiveFocus = overlay.focusedApp?.let { current ->
            apps.firstOrNull { it.packageName == current.packageName }
        } ?: sections.favorites.firstOrNull()
            ?: sections.recentApps.firstOrNull()
            ?: apps.firstOrNull()

        HomeUiState(
            isLoading = false,
            favorites = sections.favorites,
            recentApps = sections.recentApps,
            categories = sections.categories,
            allAppsCount = apps.size,
            focusedApp = effectiveFocus,
            contextMenuApp = overlay.contextMenuApp,
            preferences = prefs,
            networkStatus = network,
            defaultLauncherState = overlay.defaultLauncherState,
            capabilities = capabilities,
            statusMessage = overlay.statusMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(capabilities = capabilities),
    )

    fun onAppFocused(app: AppInfo) {
        focusedApp.value = app
    }

    fun onAppClicked(app: AppInfo) {
        // Prevent launching app if context menu dialog is active
        if (contextMenuApp.value != null) return
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

    fun onHideApp(app: AppInfo) {
        viewModelScope.launch {
            appRepository.setHidden(app.packageName, hidden = true)
            contextMenuApp.value = null
        }
    }

    fun onRefreshLauncherState() {
        defaultLauncherState.value = defaultLauncherHelper.getState()
    }

    fun onClearStatusMessage() {
        statusMessage.value = null
    }

    private data class LocalOverlayState(
        val focusedApp: AppInfo?,
        val contextMenuApp: AppInfo?,
        val statusMessage: String?,
        val defaultLauncherState: DefaultLauncherState,
    )

    internal data class HomeSections(
        val favorites: List<AppInfo>,
        val recentApps: List<AppInfo>,
        val categories: List<HomeCategorySection>,
    )

    companion object {
        private val CATEGORY_ORDER = listOf(
            "media" to "Entertainment & Media",
            "games" to "Games",
            "utilities" to "Utilities & Productivity",
            "other" to "Applications",
            "system" to "System",
        )

        internal fun buildHomeSections(
            apps: List<AppInfo>,
            prefs: LauncherPreferences,
        ): HomeSections {
            val favorites = apps.filter { it.isFavorite }

            val recentApps = if (prefs.showRecentApps) {
                apps.filter { it.lastLaunchedEpochMs > 0L }
                    .sortedByDescending { it.lastLaunchedEpochMs }
                    .take(10)
            } else {
                emptyList()
            }

            val grouped = apps.groupBy { it.categoryId ?: "other" }
            val categories = CATEGORY_ORDER.mapNotNull { (id, title) ->
                val list = grouped[id].orEmpty()
                if (list.isEmpty()) null else HomeCategorySection(id = id, title = title, apps = list)
            }

            return HomeSections(
                favorites = favorites,
                recentApps = recentApps,
                categories = categories,
            )
        }
    }
}

