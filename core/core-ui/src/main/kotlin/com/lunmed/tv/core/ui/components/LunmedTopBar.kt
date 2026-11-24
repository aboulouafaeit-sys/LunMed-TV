package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.design.tokens.LunmedGradients
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.core.ui.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LunmedTopBar(
    activeDestination: LunmedDestination,
    networkStatus: NetworkStatus,
    capabilities: DeviceCapabilities?,
    showClock: Boolean,
    showDeviceIndicators: Boolean,
    isDefaultLauncher: Boolean,
    canRequestDefaultLauncher: Boolean,
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = spacing.overscanHorizontal,
                end = spacing.overscanHorizontal,
                top = spacing.overscanVertical,
                bottom = spacing.sm,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left: Luxury Brand Badge + Glassmorphic Capsule Dock
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            LunmedBrandBadge(
                size = LunmedBrandBadgeSize.COMPACT,
                showWordmark = true,
                animatedSheen = true,
            )

            // Frosted Glass Floating Dock for Navigation
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = colors.surfaceElevated.copy(alpha = 0.55f),
                        shape = LunmedTheme.shapes.pill,
                    )
                    .border(
                        width = 1.dp,
                        color = colors.borderGlass,
                        shape = LunmedTheme.shapes.pill,
                    )
                    .padding(3.dp)
                    .lunmedFocusGroupRestorer(),
            ) {
                TopNavPill(
                    label = stringResource(R.string.nav_home),
                    icon = Icons.Rounded.Home,
                    selected = activeDestination == LunmedDestination.HOME,
                    onClick = { onNavigate(LunmedDestination.HOME) },
                )
                TopNavPill(
                    label = stringResource(R.string.nav_apps),
                    icon = Icons.Rounded.Apps,
                    selected = activeDestination == LunmedDestination.APPS,
                    onClick = { onNavigate(LunmedDestination.APPS) },
                )
                TopNavPill(
                    label = stringResource(R.string.nav_device),
                    icon = Icons.Rounded.Memory,
                    selected = activeDestination == LunmedDestination.DEVICE,
                    onClick = { onNavigate(LunmedDestination.DEVICE) },
                )
                TopNavPill(
                    label = stringResource(R.string.nav_settings),
                    icon = Icons.Rounded.Settings,
                    selected = activeDestination == LunmedDestination.SETTINGS,
                    onClick = { onNavigate(LunmedDestination.SETTINGS) },
                )
            }
        }

        // Right: Default Launcher Pill (if needed) + Device Status + Live Clock
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            if (!isDefaultLauncher && canRequestDefaultLauncher) {
                TopNavPill(
                    label = stringResource(R.string.nav_set_default),
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

    val focusRimBrush = remember(colors.accentPrimary, colors.accentSecondary) {
        LunmedGradients.focusRimBrush(colors)
    }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = anim.focusSpec(),
        label = "TopNavPillScale",
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            selected -> colors.surfaceCard.copy(alpha = 0.95f)
            accentHighlight -> colors.accentPrimary.copy(alpha = 0.16f)
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
                Border(
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.5f),
                                colors.borderSubtle,
                            ),
                        ),
                    ),
                    shape = LunmedTheme.shapes.pill,
                )
            } else if (accentHighlight) {
                Border(
                    border = BorderStroke(1.dp, colors.accentPrimary.copy(alpha = 0.6f)),
                    shape = LunmedTheme.shapes.pill,
                )
            } else {
                Border.None
            },
            focusedBorder = Border(
                border = BorderStroke(
                    width = 1.8.dp,
                    brush = focusRimBrush,
                ),
                shape = LunmedTheme.shapes.pill,
            ),
        ),
        modifier = Modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = LunmedTheme.typography.labelAction.copy(
                    fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.4.sp,
                ),
                color = contentColor,
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(colors.accentPrimary, CircleShape),
                )
            }
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
            .background(
                color = colors.surfaceElevated.copy(alpha = 0.65f),
                shape = LunmedTheme.shapes.pill,
            )
            .border(
                width = 1.dp,
                color = colors.borderGlass,
                shape = LunmedTheme.shapes.pill,
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        if (is4k) {
            Box(
                modifier = Modifier
                    .background(
                        color = colors.accentPrimary.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .border(
                        width = 0.8.dp,
                        color = colors.accentPrimary.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp),
            ) {
                Text(
                    text = "4K HDR",
                    style = LunmedTheme.typography.labelMeta.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp,
                    ),
                    color = colors.accentPrimary,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (networkStatus.isConnected) colors.statusSuccess else colors.statusWarning,
                        shape = CircleShape,
                    ),
            )
            Icon(
                imageVector = networkIcon,
                contentDescription = networkStatus.transport.name,
                tint = networkTint,
                modifier = Modifier.size(16.dp),
            )
        }
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
            style = LunmedTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
            ),
            color = colors.textPrimary,
        )
        Text(
            text = dateFormatter.format(date),
            style = LunmedTheme.typography.labelMeta.copy(
                letterSpacing = 0.4.sp,
            ),
            color = colors.accentPrimary.copy(alpha = 0.85f),
        )
    }
}
