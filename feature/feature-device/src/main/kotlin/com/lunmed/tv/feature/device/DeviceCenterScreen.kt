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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.lunmed.tv.feature.device.components.DiagnosticInfoCard
import com.lunmed.tv.feature.device.components.DiagnosticItem
import com.lunmed.tv.feature.device.components.GaugeMetricCard
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
                        label = tab.label,
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
                    text = uiState.statusNotification.orEmpty(),
                    style = LunmedTheme.typography.bodySecondary,
                    color = colors.statusSuccess,
                )
            }
        }
    }
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
                label = if (uiState.isOptimizing) "Optimizing..." else "Free Memory Cache",
                icon = Icons.Rounded.CleaningServices,
                onClick = onClearMemoryCache,
            )
            ActionCard(
                label = "Refresh Metrics",
                icon = Icons.Rounded.Refresh,
                onClick = onRefreshDiagnostics,
            )
            ActionCard(
                label = "Native Settings",
                icon = Icons.Rounded.Settings,
                onClick = onOpenAndroidSettings,
            )
        }

        if (rep != null) {
            // RAM Gauge
            GaugeMetricCard(
                title = "RAM Memory Engine",
                subtitle = "Active operational memory allocation & buffers",
                icon = Icons.Rounded.Memory,
                usedFormatted = rep.memory.usedGbFormatted,
                totalFormatted = rep.memory.totalGbFormatted,
                availableFormatted = rep.memory.availableGbFormatted,
                usageRatio = rep.memory.usageRatio,
                statusText = if (rep.memory.isLowMemory) "LOW MEMORY" else "OPTIMAL",
                isWarning = rep.memory.isLowMemory,
                onClick = onClearMemoryCache,
            )

            // Storage Gauge
            GaugeMetricCard(
                title = "Internal TV Storage",
                subtitle = "Flash memory filesystem utilization",
                icon = Icons.Rounded.Storage,
                usedFormatted = rep.storage.usedGbFormatted,
                totalFormatted = rep.storage.totalGbFormatted,
                availableFormatted = rep.storage.availableGbFormatted,
                usageRatio = rep.storage.usageRatio,
                statusText = if (rep.storage.usageRatio > 0.9f) "CRITICAL" else "HEALTHY",
                isWarning = rep.storage.usageRatio > 0.9f,
                onClick = onOpenAndroidSettings,
            )

            // Network Diagnostics Card
            DiagnosticInfoCard(
                title = "Network Connection & Telemetry",
                subtitle = "Active transport, IP routing, and wireless performance",
                icon = if (rep.network.transportName == "Ethernet") Icons.Rounded.Lan else Icons.Rounded.Wifi,
                items = listOf(
                    DiagnosticItem("Connection Transport", rep.network.transportName, isHighlight = true),
                    DiagnosticItem("Local IP Address", rep.network.ipAddress),
                    DiagnosticItem("Internet Access", if (rep.network.isConnected) "Active & Reachable" else "Offline"),
                    DiagnosticItem("Wi-Fi SSID", rep.network.wifiSsid ?: "N/A"),
                    DiagnosticItem("Wi-Fi Signal Strength", rep.network.wifiRssiDbm?.let { "$it dBm" } ?: "N/A"),
                    DiagnosticItem("Link Speed", rep.network.wifiLinkSpeedMbps?.let { "$it Mbps" } ?: "N/A"),
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
            text = "Signature Obsidian Wallpapers",
            style = LunmedTheme.typography.titleLarge,
            color = LunmedTheme.colors.textPrimary,
        )
        Text(
            text = "Fine luxury palettes engineered to enhance cinematic depth and contrast.",
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
            DiagnosticInfoCard(
                title = "Display & Video Output",
                subtitle = "Resolution, refresh rate, and dynamic range capabilities",
                icon = Icons.Rounded.Tv,
                items = listOf(
                    DiagnosticItem("Screen Resolution", "${rep.display.resolutionWidth} x ${rep.display.resolutionHeight}", isHighlight = true),
                    DiagnosticItem("4K Ultra HD", if (rep.display.is4k) "Supported (UHD)" else "Full HD (FHD)"),
                    DiagnosticItem("Refresh Rate", String.format(java.util.Locale.US, "%.1f Hz", rep.display.refreshRateHz)),
                    DiagnosticItem("Pixel Density", "${rep.display.densityDpi} DPI"),
                    DiagnosticItem("HDR Modes", rep.display.hdrSupportSummary, isHighlight = rep.display.hdrSupportSummary != "SDR"),
                ),
            )

            DiagnosticInfoCard(
                title = "Hardware & Platform Architecture",
                subtitle = "Chipset, motherboard, and operating system build parameters",
                icon = Icons.Rounded.DeveloperBoard,
                items = listOf(
                    DiagnosticItem("Manufacturer / Brand", "${rep.platform.manufacturer} (${rep.platform.brand})"),
                    DiagnosticItem("Model & Product", "${rep.platform.model} [${rep.platform.product}]"),
                    DiagnosticItem("Board / SoC", rep.platform.socOrBoard, isHighlight = true),
                    DiagnosticItem("CPU Architecture (ABI)", rep.platform.supportedAbis),
                    DiagnosticItem("Android OS Version", "${rep.platform.androidRelease} (API Level ${rep.platform.sdkInt})", isHighlight = true),
                    DiagnosticItem("Security Patch Level", rep.platform.securityPatch),
                    DiagnosticItem("Firmware Build ID", rep.platform.buildDisplay),
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

