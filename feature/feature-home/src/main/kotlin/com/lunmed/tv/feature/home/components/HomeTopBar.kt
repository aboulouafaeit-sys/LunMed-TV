package com.lunmed.tv.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HomeNavDestination {
    HOME,
    APPS,
    DEVICE,
    SETTINGS,
}

@Composable
fun HomeTopBar(
    activeDestination: HomeNavDestination,
    networkStatus: NetworkStatus,
    capabilities: DeviceCapabilities?,
    showClock: Boolean,
    showDeviceIndicators: Boolean,
    isDefaultLauncher: Boolean,
    canRequestDefaultLauncher: Boolean,
    onNavigate: (HomeNavDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = spacing.overscanHorizontal,
                vertical = spacing.md,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left: Brand Wordmark + Smart Pill Navigation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(colors.accentPrimary, LunmedTheme.shapes.pill),
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = "LUNMED",
                    style = LunmedTheme.typography.labelAction.copy(
                        letterSpacing = 2.6.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = colors.textPrimary,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.lunmedFocusGroupRestorer(),
            ) {
                TopNavPill(
                    label = "Home",
                    icon = Icons.Rounded.Home,
                    selected = activeDestination == HomeNavDestination.HOME,
                    onClick = { onNavigate(HomeNavDestination.HOME) },
                )
                TopNavPill(
                    label = "Apps",
                    icon = Icons.Rounded.Apps,
                    selected = activeDestination == HomeNavDestination.APPS,
                    onClick = { onNavigate(HomeNavDestination.APPS) },
                )
                TopNavPill(
                    label = "Device",
                    icon = Icons.Rounded.Memory,
                    selected = activeDestination == HomeNavDestination.DEVICE,
                    onClick = { onNavigate(HomeNavDestination.DEVICE) },
                )
                TopNavPill(
                    label = "Settings",
                    icon = Icons.Rounded.Settings,
                    selected = activeDestination == HomeNavDestination.SETTINGS,
                    onClick = { onNavigate(HomeNavDestination.SETTINGS) },
                )
            }
        }

        // Right: Default Launcher Pill (if needed) + Device Indicators + Live Clock
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            if (!isDefaultLauncher && canRequestDefaultLauncher) {
                TopNavPill(
                    label = "Set Default",
                    icon = Icons.Rounded.Home,
                    selected = false,
                    accentHighlight = true,
                    onClick = onRequestDefaultLauncher,
                )
            }

            if (showDeviceIndicators) {
                DeviceStatusCluster(
                    networkStatus = networkStatus,
                    is4k = capabilities?.is4kDisplay == true,
                )
            }

            if (showClock) {
                LunmedLiveClock()
            }
        }
    }
}

@Composable
private fun TopNavPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    accentHighlight: Boolean = false,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            selected -> colors.surfaceElevated
            accentHighlight -> colors.accentPrimary.copy(alpha = 0.14f)
            else -> Color.Transparent
        },
        animationSpec = anim.focusSpec(),
        label = "TopNavPillBg",
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.accentPrimary
            selected -> colors.textPrimary
            accentHighlight -> colors.accentPrimary
            else -> colors.textSecondary
        },
        animationSpec = anim.focusSpec(),
        label = "TopNavPillContent",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = contentColor,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (selected) {
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
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = LunmedTheme.typography.labelAction,
                color = contentColor,
            )
        }
    }
}

@Composable
private fun DeviceStatusCluster(
    networkStatus: NetworkStatus,
    is4k: Boolean,
) {
    val colors = LunmedTheme.colors
    val networkIcon = when (networkStatus.transport) {
        NetworkTransport.ETHERNET -> Icons.Rounded.Lan
        NetworkTransport.WIFI -> Icons.Rounded.Wifi
        NetworkTransport.CELLULAR -> Icons.Rounded.SignalCellularAlt
        NetworkTransport.OTHER -> Icons.Rounded.Wifi
        NetworkTransport.OFFLINE -> Icons.Rounded.WifiOff
    }
    val networkTint = if (networkStatus.isConnected) colors.textSecondary else colors.statusWarning

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(colors.surfaceElevated.copy(alpha = 0.7f), LunmedTheme.shapes.pill)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        if (is4k) {
            Text(
                text = "4K",
                style = LunmedTheme.typography.labelMeta,
                color = colors.accentPrimary,
            )
        }
        Icon(
            imageVector = networkIcon,
            contentDescription = networkStatus.transport.name,
            tint = networkTint,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun LunmedLiveClock() {
    val colors = LunmedTheme.colors
    var nowMs by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            nowMs = System.currentTimeMillis()
            val delayToNextMinute = 60_000L - (nowMs % 60_000L)
            delay(delayToNextMinute.coerceAtLeast(1_000L))
        }
    }

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val date = remember(nowMs) { Date(nowMs) }

    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = timeFormatter.format(date),
            style = LunmedTheme.typography.titleLarge,
            color = colors.textPrimary,
        )
        Text(
            text = dateFormatter.format(date),
            style = LunmedTheme.typography.labelMeta,
            color = colors.textSecondary,
        )
    }
}

