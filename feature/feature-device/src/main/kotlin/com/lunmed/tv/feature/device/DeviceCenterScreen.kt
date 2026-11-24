package com.lunmed.tv.feature.device

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.DeveloperBoard
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.core.navigation.remote.RemoteCommand
import com.lunmed.tv.core.navigation.remote.onRemoteKeyCommand
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedTopBar
import com.lunmed.tv.core.ui.R
import com.lunmed.tv.feature.device.components.DiagnosticInfoCard
import com.lunmed.tv.feature.device.components.DiagnosticItem
import com.lunmed.tv.feature.device.components.GaugeMetricCard
import com.lunmed.tv.feature.device.components.SiliconSoCChipBadge
import com.lunmed.tv.feature.device.components.WallpaperCard
import kotlinx.coroutines.delay

@Composable
fun DeviceCenterRoute(
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAndroidSettings: () -> Unit,
    viewModel: DeviceCenterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onRefreshLauncherState()
    }

    LaunchedEffect(uiState.statusNotification) {
        if (uiState.statusNotification != null) {
            delay(3_000L)
            viewModel.onClearStatusMessage()
        }
    }

    DeviceCenterScreen(
        uiState = uiState,
        onNavigate = onNavigate,
        onRequestDefaultLauncher = onRequestDefaultLauncher,
        onOpenAndroidSettings = onOpenAndroidSettings,
        onSelectTab = viewModel::onSelectTab,
        onRefreshDiagnostics = viewModel::onRefreshDiagnostics,
        onClearMemoryCache = viewModel::onClearMemoryCache,
        onSelectWallpaper = viewModel::onSelectWallpaper,
    )
}

@Composable
fun DeviceCenterScreen(
    uiState: DeviceCenterUiState,
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAndroidSettings: () -> Unit,
    onSelectTab: (DeviceCenterTab) -> Unit,
    onRefreshDiagnostics: () -> Unit,
    onClearMemoryCache: () -> Unit,
    onSelectWallpaper: (String) -> Unit,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing

    BackHandler(enabled = true) {
        onNavigate(LunmedDestination.HOME)
    }

    LunmedAmbientCanvas(
        wallpaper = uiState.selectedWallpaper,
        modifier = Modifier.onRemoteKeyCommand { command ->
            when (command) {
                RemoteCommand.SETTINGS -> {
                    onNavigate(LunmedDestination.SETTINGS)
                    true
                }
                RemoteCommand.SEARCH -> {
                    onNavigate(LunmedDestination.APPS)
                    true
                }
                else -> false
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LunmedTopBar(
                activeDestination = LunmedDestination.DEVICE,
                networkStatus = uiState.networkStatus,
                capabilities = uiState.capabilities,
                showClock = uiState.preferences.showClock,
                showDeviceIndicators = uiState.preferences.showDeviceIndicators,
                isDefaultLauncher = uiState.defaultLauncherState.isDefaultLauncher,
                canRequestDefaultLauncher = uiState.defaultLauncherState.canRequestRole,
                onNavigate = onNavigate,
                onRequestDefaultLauncher = onRequestDefaultLauncher,
            )

            // Sub Tab Bar (Resources, Wallpapers, Hardware)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.overscanHorizontal)
                    .lunmedFocusGroupRestorer(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                DeviceCenterTab.entries.forEach { tab ->
                    TabPill(
                        label = stringResource(tab.labelRes),
                        icon = when (tab) {
                            DeviceCenterTab.RESOURCES -> Icons.Rounded.Memory
                            DeviceCenterTab.WALLPAPERS -> Icons.Rounded.Wallpaper
                            DeviceCenterTab.HARDWARE -> Icons.Rounded.DeveloperBoard
                        },
                        isSelected = uiState.activeTab == tab,
                        onClick = { onSelectTab(tab) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.overscanHorizontal),
            ) {
                when (uiState.activeTab) {
                    DeviceCenterTab.RESOURCES -> {
                        ResourcesTabContent(
                            uiState = uiState,
                            onClearMemoryCache = onClearMemoryCache,
                            onRefreshDiagnostics = onRefreshDiagnostics,
                            onOpenAndroidSettings = onOpenAndroidSettings,
                        )
                    }
                    DeviceCenterTab.WALLPAPERS -> {
                        WallpapersTabContent(
                            uiState = uiState,
                            onSelectWallpaper = onSelectWallpaper,
                        )
                    }
                    DeviceCenterTab.HARDWARE -> {
                        HardwareTabContent(
                            uiState = uiState,
                            onOpenAndroidSettings = onOpenAndroidSettings,
                        )
                    }
                }
            }
        }

        // Status Toast
        AnimatedVisibility(
            visible = uiState.statusNotification != null,
            enter = fadeIn(LunmedTheme.animation.focusSpec()),
            exit = fadeOut(LunmedTheme.animation.focusSpec()),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = spacing.xl),
        ) {
            Box(
                modifier = Modifier
                    .background(colors.surfaceElevated, LunmedTheme.shapes.pill)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = uiState.statusNotification?.asDisplayText().orEmpty(),
                    style = LunmedTheme.typography.bodySecondary,
                    color = colors.statusSuccess,
                )
            }
        }
    }
}

@Composable
private fun DeviceStatusMessage.asDisplayText(): String = when (this) {
    DeviceStatusMessage.TelemetryRefreshed -> stringResource(R.string.device_msg_refreshed)

    is DeviceStatusMessage.CacheOptimized -> freedMbFormatted
        ?.let { stringResource(R.string.device_msg_optimized_mb, it) }
        ?: stringResource(R.string.device_msg_optimized)

    is DeviceStatusMessage.WallpaperApplied ->
        stringResource(R.string.device_msg_wallpaper_applied, wallpaperTitle)
}

@Composable
private fun TabPill(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) colors.surfaceElevated else Color.Transparent,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = if (isSelected) colors.accentPrimary else colors.textSecondary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = LunmedTheme.shapes.pill)
            } else {
                Border.None
            },
            focusedBorder = Border(
                border = BorderStroke(1.5.dp, colors.borderFocused),
                shape = LunmedTheme.shapes.pill,
            ),
        ),
        modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = LunmedTheme.typography.labelAction,
            )
        }
    }
}

@Composable
private fun ResourcesTabContent(
    uiState: DeviceCenterUiState,
    onClearMemoryCache: () -> Unit,
    onRefreshDiagnostics: () -> Unit,
    onOpenAndroidSettings: () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val rep = uiState.report

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .lunmedFocusGroupRestorer()
            .padding(bottom = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        // Quick Action Bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionCard(
                label = if (uiState.isOptimizing) {
                    stringResource(R.string.device_action_optimizing)
                } else {
                    stringResource(R.string.device_action_free_cache)
                },
                icon = Icons.Rounded.CleaningServices,
                onClick = onClearMemoryCache,
            )
            ActionCard(
                label = stringResource(R.string.device_action_refresh),
                icon = Icons.Rounded.Refresh,
                onClick = onRefreshDiagnostics,
            )
            ActionCard(
                label = stringResource(R.string.device_action_native_settings),
                icon = Icons.Rounded.Settings,
                onClick = onOpenAndroidSettings,
            )
        }

        if (rep != null) {
            // RAM Gauge
            GaugeMetricCard(
                title = stringResource(R.string.device_ram_title),
                subtitle = stringResource(R.string.device_ram_subtitle),
                icon = Icons.Rounded.Memory,
                usedFormatted = rep.memory.usedGbFormatted,
                totalFormatted = rep.memory.totalGbFormatted,
                availableFormatted = rep.memory.availableGbFormatted,
                usageRatio = rep.memory.usageRatio,
                statusText = if (rep.memory.isLowMemory) {
                    stringResource(R.string.device_ram_low)
                } else {
                    stringResource(R.string.device_ram_optimal)
                },
                isWarning = rep.memory.isLowMemory,
                onClick = onClearMemoryCache,
            )

            // Storage Gauge
            GaugeMetricCard(
                title = stringResource(R.string.device_storage_title),
                subtitle = stringResource(R.string.device_storage_subtitle),
                icon = Icons.Rounded.Storage,
                usedFormatted = rep.storage.usedGbFormatted,
                totalFormatted = rep.storage.totalGbFormatted,
                availableFormatted = rep.storage.availableGbFormatted,
                usageRatio = rep.storage.usageRatio,
                statusText = if (rep.storage.usageRatio > 0.9f) {
                    stringResource(R.string.device_storage_critical)
                } else {
                    stringResource(R.string.device_storage_healthy)
                },
                isWarning = rep.storage.usageRatio > 0.9f,
                onClick = onOpenAndroidSettings,
            )

            // Network Diagnostics Card
            DiagnosticInfoCard(
                title = stringResource(R.string.device_network_title),
                subtitle = stringResource(R.string.device_network_subtitle),
                icon = if (rep.network.transportName == "Ethernet") Icons.Rounded.Lan else Icons.Rounded.Wifi,
                items = listOf(
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_transport),
                        value = rep.network.transportName,
                        isHighlight = true,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_ip),
                        value = rep.network.ipAddress,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_internet),
                        value = if (rep.network.isConnected) {
                            stringResource(R.string.device_net_active)
                        } else {
                            stringResource(R.string.device_net_offline)
                        },
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_ssid),
                        value = rep.network.wifiSsid ?: stringResource(R.string.device_value_na),
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_signal),
                        value = rep.network.wifiRssiDbm?.let { "$it dBm" } ?: stringResource(R.string.device_value_na),
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_net_link),
                        value = rep.network.wifiLinkSpeedMbps?.let { "$it Mbps" } ?: stringResource(R.string.device_value_na),
                    ),
                ),
            )
        }
    }
}

@Composable
private fun WallpapersTabContent(
    uiState: DeviceCenterUiState,
    onSelectWallpaper: (String) -> Unit,
) {
    val spacing = LunmedTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = spacing.xl),
    ) {
        Text(
            text = stringResource(R.string.device_wallpapers_title),
            style = LunmedTheme.typography.titleLarge,
            color = LunmedTheme.colors.textPrimary,
        )
        Text(
            text = stringResource(R.string.device_wallpapers_subtitle),
            style = LunmedTheme.typography.bodySecondary,
            color = LunmedTheme.colors.textMuted,
        )

        Spacer(modifier = Modifier.height(spacing.md))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
            modifier = Modifier
                .fillMaxSize()
                .lunmedFocusGroupRestorer(),
        ) {
            items(uiState.wallpapers, key = { it.id }) { wallpaper ->
                WallpaperCard(
                    wallpaper = wallpaper,
                    isSelected = uiState.selectedWallpaper?.id == wallpaper.id,
                    onSelect = { onSelectWallpaper(wallpaper.id) },
                )
            }
        }
    }
}

@Composable
private fun HardwareTabContent(
    uiState: DeviceCenterUiState,
    onOpenAndroidSettings: () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val rep = uiState.report

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .lunmedFocusGroupRestorer()
            .padding(bottom = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        if (rep != null) {
            // 1. Silicon Processor Chip Die Badge
            SiliconSoCChipBadge(
                platform = rep.platform,
                onClick = onOpenAndroidSettings,
            )

            // 2. Display & Video Pipeline
            DiagnosticInfoCard(
                title = stringResource(R.string.device_display_title),
                subtitle = stringResource(R.string.device_display_subtitle),
                icon = Icons.Rounded.Tv,
                items = listOf(
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_resolution),
                        value = "${rep.display.resolutionWidth} x ${rep.display.resolutionHeight}",
                        isHighlight = true,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_4k),
                        value = if (rep.display.is4k) {
                            stringResource(R.string.device_display_4k_supported)
                        } else {
                            stringResource(R.string.device_display_fhd)
                        },
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_refresh),
                        value = String.format(java.util.Locale.US, "%.1f Hz", rep.display.refreshRateHz),
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_density),
                        value = "${rep.display.densityDpi} DPI",
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_hdr),
                        value = rep.display.hdrSupportSummary,
                        isHighlight = rep.display.hdrSupportSummary != "SDR",
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_display_audio),
                        value = rep.display.audioOutputPipeline,
                        isHighlight = true,
                    ),
                ),
            )

            // 3. Platform & Kernel Details
            DiagnosticInfoCard(
                title = stringResource(R.string.device_platform_title),
                subtitle = stringResource(R.string.device_platform_subtitle),
                icon = Icons.Rounded.DeveloperBoard,
                items = listOf(
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_manufacturer),
                        value = "${rep.platform.manufacturer} (${rep.platform.brand})",
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_model),
                        value = "${rep.platform.model} [${rep.platform.product}]",
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_board),
                        value = rep.platform.socOrBoard,
                        isHighlight = true,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_cpu),
                        value = stringResource(R.string.device_platform_cpu_units, rep.platform.cpuCores),
                        isHighlight = true,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_abi),
                        value = rep.platform.supportedAbis,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_android),
                        value = stringResource(
                            R.string.device_platform_android_value,
                            rep.platform.androidRelease,
                            rep.platform.sdkInt,
                        ),
                        isHighlight = true,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_patch),
                        value = rep.platform.securityPatch,
                    ),
                    DiagnosticItem(
                        label = stringResource(R.string.device_platform_build),
                        value = rep.platform.buildDisplay,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun ActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceElevated,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = LunmedTheme.shapes.pill),
            focusedBorder = Border(border = BorderStroke(1.5.dp, colors.borderFocused), shape = LunmedTheme.shapes.pill),
        ),
        modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = if (isFocused) colors.accentPrimary else colors.textSecondary,
            )
            Text(
                text = label,
                style = LunmedTheme.typography.labelAction,
                color = if (isFocused) colors.accentPrimary else colors.textPrimary,
            )
        }
    }
}

