package com.lunmed.tv.feature.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.DeviceDiagnosticsRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.repository.WallpaperRepository
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import com.lunmed.tv.core.model.FullDeviceReport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DeviceCenterViewModel @Inject constructor(
    private val deviceDiagnosticsRepository: DeviceDiagnosticsRepository,
    private val wallpaperRepository: WallpaperRepository,
    private val preferencesRepository: PreferencesRepository,
    private val defaultLauncherHelper: DefaultLauncherHelper,
    capabilityDetector: DeviceCapabilityDetector,
    networkStatusMonitor: NetworkStatusMonitor,
) : ViewModel() {

    private val capabilities = capabilityDetector.detect()
    private val activeTab = MutableStateFlow(DeviceCenterTab.RESOURCES)
    private val deviceReport = MutableStateFlow<FullDeviceReport?>(null)
    private val isOptimizing = MutableStateFlow(false)
    private val statusNotification = MutableStateFlow<DeviceStatusMessage?>(null)
    private val launcherStateTick = MutableStateFlow(0)

    init {
        refreshDiagnostics()
    }

    private data class LocalTabAndStatus(
        val tab: DeviceCenterTab,
        val report: FullDeviceReport?,
        val optimizing: Boolean,
        val notification: DeviceStatusMessage?,
        val launcherTick: Int,
    )

    private val localFlow = combine(
        activeTab,
        deviceReport,
        isOptimizing,
        statusNotification,
        launcherStateTick,
    ) { tab, rep, opt, notif, tick ->
        LocalTabAndStatus(tab, rep, opt, notif, tick)
    }

    val uiState: StateFlow<DeviceCenterUiState> = combine(
        localFlow,
        wallpaperRepository.getSelectedWallpaper(),
        preferencesRepository.preferences,
        networkStatusMonitor.networkStatus,
    ) { local, selectedWp, prefs, net ->
        DeviceCenterUiState(
            activeTab = local.tab,
            report = local.report,
            wallpapers = wallpaperRepository.getBuiltInWallpapers(),
            selectedWallpaper = selectedWp,
            preferences = prefs,
            networkStatus = net,
            capabilities = capabilities,
            defaultLauncherState = defaultLauncherHelper.getState(),
            isOptimizing = local.optimizing,
            statusNotification = local.notification,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DeviceCenterUiState(
            capabilities = capabilities,
            wallpapers = wallpaperRepository.getBuiltInWallpapers(),
            report = deviceReport.value,
        ),
    )

    fun onSelectTab(tab: DeviceCenterTab) {
        activeTab.value = tab
    }

    fun onRefreshDiagnostics() {
        refreshDiagnostics()
        statusNotification.value = DeviceStatusMessage.TelemetryRefreshed
    }

    /** Silent re-read used when the screen resumes (HOME role may have just changed). */
    fun onRefreshLauncherState() {
        launcherStateTick.value += 1
        refreshDiagnostics()
    }

    fun onClearMemoryCache() {
        if (isOptimizing.value) return
        viewModelScope.launch {
            isOptimizing.value = true
            com.lunmed.tv.core.ui.artwork.AppArtworkCache.clear()
            val bytesFreed = deviceDiagnosticsRepository.clearMemoryCache()
            delay(400) // Brief smooth tactile transition
            val freedMb = bytesFreed.toDouble() / (1024 * 1024)
            statusNotification.value = DeviceStatusMessage.CacheOptimized(
                freedMbFormatted = if (freedMb > 0.1) String.format(Locale.US, "%.1f", freedMb) else null,
            )
            refreshDiagnostics()
            isOptimizing.value = false
        }
    }

    fun onSelectWallpaper(wallpaperId: String) {
        viewModelScope.launch {
            wallpaperRepository.selectWallpaper(wallpaperId)
            val wp = wallpaperRepository.getWallpaperById(wallpaperId)
            statusNotification.value = DeviceStatusMessage.WallpaperApplied(wallpaperTitle = wp?.title ?: wallpaperId)
        }
    }

    fun onClearStatusMessage() {
        statusNotification.value = null
    }

    private fun refreshDiagnostics() {
        deviceReport.value = deviceDiagnosticsRepository.getDeviceReport()
    }
}

