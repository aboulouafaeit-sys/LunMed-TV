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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.IconDisplayMode
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.LunmedWallpapers
import com.lunmed.tv.core.model.NetworkStatus

/**
 * Signature Apple TV (tvOS) / iPhone Control Center floating overlay.
 * Provides instant tactile quick access to:
 * 1. Apple-style quick connectivity, sound, and display pods.
 * 2. Instant App Icon presentation style switcher (Banner / Squircle / Capsule).
 * 3. Live Scenic Wallpaper carousel switcher.
 * 4. System default launcher & root lockdown status.
 * 5. Quick power & Android system settings actions.
 */
@Composable
fun AppleControlCenterOverlay(
    preferences: LauncherPreferences,
    networkStatus: NetworkStatus?,
    capabilities: DeviceCapabilities?,
    defaultLauncherState: DefaultLauncherState?,
    onSetIconDisplayMode: (IconDisplayMode) -> Unit,
    onSelectWallpaper: (String) -> Unit,
    onOpenAndroidSettings: () -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(480.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF012131C),
                            Color(0xF50A0B10),
                            Color(0xFC050508),
                        ),
                    ),
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.65f),
                                Color(0x33FFFFFF),
                                Color(0x10FFFFFF),
                            ),
                        ),
                    ),
                    RoundedCornerShape(28.dp),
                )
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Header: Apple Control Center Title & User Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "CONTROL CENTER",
                            style = LunmedTheme.typography.labelAction.copy(
                                letterSpacing = 2.sp,
                                fontSize = 11.sp,
                            ),
                            color = colors.accentPrimary,
                        )
                        Text(
                            text = "Apple tvOS Quick Suite",
                            style = LunmedTheme.typography.headlineSection.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(colors.surfaceElevated, CircleShape)
                            .border(1.dp, Color(0x33FFFFFF), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "مركز التحكم الفائق",
                            style = LunmedTheme.typography.labelMeta.copy(fontWeight = FontWeight.Bold),
                            color = colors.accentPrimary,
                        )
                    }
                }

                // 2. Connectivity & Display Pods (2x2 Grid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ApplePodTile(
                        icon = Icons.Rounded.Wifi,
                        title = if (networkStatus?.isConnected == true) "Connected" else "Offline",
                        subtitle = networkStatus?.transport?.name ?: "Wi-Fi / LAN",
                        modifier = Modifier.weight(1f),
                        isActive = networkStatus?.isConnected == true,
                    )
                    ApplePodTile(
                        icon = Icons.Rounded.VolumeUp,
                        title = "Audio Output",
                        subtitle = "Dolby Atmos / PCM",
                        modifier = Modifier.weight(1f),
                        isActive = true,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ApplePodTile(
                        icon = Icons.Rounded.DisplaySettings,
                        title = if (capabilities?.is4kDisplay == true) "4K UHD HDR" else "1080p Full HD",
                        subtitle = "60Hz Cinematic",
                        modifier = Modifier.weight(1f),
                        isActive = true,
                    )
                    ApplePodTile(
                        icon = Icons.Rounded.Tune,
                        title = if (defaultLauncherState?.isDefaultLauncher == true) "System Default" else "Custom Launcher",
                        subtitle = if (defaultLauncherState?.isDefaultLauncher == true) "Active (نشط)" else "Action Required",
                        modifier = Modifier.weight(1f),
                        isActive = defaultLauncherState?.isDefaultLauncher == true,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x33FFFFFF), Color.Transparent),
                            ),
                        ),
                )

                // 3. App Icon Presentation Switcher
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "App Icon Presentation Mode (نمط عرض الأيقونات)",
                        style = LunmedTheme.typography.labelAction,
                        color = colors.textSecondary,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        IconModeButton(
                            title = "16:9 Banner",
                            mode = IconDisplayMode.CINEMA_BANNER,
                            isSelected = preferences.iconDisplayMode == IconDisplayMode.CINEMA_BANNER,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(firstFocus),
                            onClick = { onSetIconDisplayMode(IconDisplayMode.CINEMA_BANNER) },
                        )
                        IconModeButton(
                            title = "iOS Squircle",
                            mode = IconDisplayMode.SQUARE_IOS,
                            isSelected = preferences.iconDisplayMode == IconDisplayMode.SQUARE_IOS,
                            modifier = Modifier.weight(1f),
                            onClick = { onSetIconDisplayMode(IconDisplayMode.SQUARE_IOS) },
                        )
                        IconModeButton(
                            title = "Capsule",
                            mode = IconDisplayMode.COMPACT_CAPSULE,
                            isSelected = preferences.iconDisplayMode == IconDisplayMode.COMPACT_CAPSULE,
                            modifier = Modifier.weight(1f),
                            onClick = { onSetIconDisplayMode(IconDisplayMode.COMPACT_CAPSULE) },
                        )
                    }
                }

                // 4. Breathtaking Scenic Wallpaper Quick Switcher
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Scenic Dynamic Wallpapers (الخلفيات الخلابة)",
                        style = LunmedTheme.typography.labelAction,
                        color = colors.textSecondary,
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(LunmedWallpapers.BUILT_IN) { wallpaper ->
                            val isSelected = preferences.selectedWallpaperId == wallpaper.id
                            WallpaperQuickCard(
                                wallpaper = wallpaper,
                                isSelected = isSelected,
                                onClick = { onSelectWallpaper(wallpaper.id) },
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x33FFFFFF), Color.Transparent),
                            ),
                        ),
                )

                // 5. System Quick Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (defaultLauncherState?.isDefaultLauncher == false && defaultLauncherState.canRequestRole) {
                        AppleActionRow(
                            icon = Icons.Rounded.Tune,
                            title = "Make LUNMED TV Default Launcher",
                            subtitle = "تعيين المشغل كافتراضي لنظام أندرويد",
                            onClick = onRequestDefaultLauncher,
                        )
                    }

                    AppleActionRow(
                        icon = Icons.Rounded.Settings,
                        title = "Open Android System Settings",
                        subtitle = "إعدادات أندرويد العامة (شبكة، بلوتوث، شاشة)",
                        onClick = onOpenAndroidSettings,
                    )

                    AppleActionRow(
                        icon = Icons.Rounded.Clear,
                        title = "Close Control Center",
                        subtitle = "إغلاق مركز التحكم والرجوع",
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplePodTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (isActive) colors.surfaceCardFocused else colors.surfaceElevated,
                        colors.surfaceCard,
                    ),
                ),
            )
            .border(1.dp, if (isActive) colors.accentPrimary.copy(alpha = 0.35f) else Color(0x18FFFFFF), shape)
            .padding(14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) colors.accentPrimary.copy(alpha = 0.20f) else Color(0x15FFFFFF),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) colors.accentPrimary else colors.textMuted,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                    color = colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun IconModeButton(
    title: String,
    mode: IconDisplayMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(12.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "IconModeButtonScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) colors.accentPrimary.copy(alpha = 0.22f) else colors.surfaceElevated,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = if (isSelected) colors.accentPrimary else colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, if (isSelected) colors.accentPrimary else Color(0x20FFFFFF)),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, colors.borderFocused),
                shape = shape,
            ),
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isSelected) "✓ $title" else title,
                style = LunmedTheme.typography.labelAction.copy(
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                ),
                color = if (isSelected || isFocused) colors.accentPrimary else colors.textPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun WallpaperQuickCard(
    wallpaper: LunmedWallpaper,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(12.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "WallpaperCardScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color(wallpaper.primaryColorArgb),
            focusedContainerColor = Color(wallpaper.primaryColorArgb),
            contentColor = colors.textPrimary,
            focusedContentColor = colors.textPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) colors.accentPrimary else Color(0x28FFFFFF),
                ),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, colors.borderFocused),
                shape = shape,
            ),
        ),
        modifier = Modifier
            .size(width = 118.dp, height = 74.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(wallpaper.ambientGlowArgb).copy(alpha = 0.40f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(8.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                text = wallpaper.title.split(" ").firstOrNull() ?: wallpaper.title,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
                color = if (isSelected) colors.accentPrimary else Color.White,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun AppleActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(14.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "AppleActionScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceCard,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, Color(0x18FFFFFF)), shape = shape),
            focusedBorder = Border(border = BorderStroke(1.5.dp, colors.borderFocused), shape = shape),
        ),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFocused) colors.accentPrimary.copy(alpha = 0.20f) else Color(0x12FFFFFF),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) colors.accentPrimary else colors.textSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary.copy(
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                    ),
                    color = if (isFocused) colors.accentPrimary else colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                    color = colors.textMuted,
                )
            }
        }
    }
}

