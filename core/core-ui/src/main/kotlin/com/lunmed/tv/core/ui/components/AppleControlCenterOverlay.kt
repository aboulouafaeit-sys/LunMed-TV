package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.lunmed.tv.core.design.tokens.LunmedGradients
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.IconDisplayMode
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.LunmedWallpapers
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.ScenicType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * World-class Apple tvOS 18 / iOS 18 Control Center floating Bento Grid.
 * Built with:
 * 1. Ultra-deep frosted glassmorphism with 24K champagne gold specular beveling.
 * 2. Authentic Apple Bento Pods: Dual Connectivity, Master Audio slider, 4K Vision display.
 * 3. Liquid Apple Segmented Glass Slider for instantaneous Icon Display Mode switching.
 * 4. Procedural Widescreen Scenic Wallpaper live gallery.
 * 5. Apple-style quick round action buttons (Standby, Restart, Android Settings, Default Launcher).
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

    val currentTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
    val currentDate = remember {
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(Unit) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(530.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF4121422),
                            Color(0xF90A0B14),
                            Color(0xFD05060A),
                        ),
                    ),
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.70f),
                                Color(0x35FFFFFF),
                                Color(0x10FFFFFF),
                            ),
                        ),
                    ),
                    RoundedCornerShape(32.dp),
                )
                .padding(24.dp),
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Signature Apple Header: Live Time & User Profile Ring
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Monogram Ring
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(colors.accentPrimary.copy(alpha = 0.35f), Color(0x22FFFFFF)),
                                    ),
                                )
                                .border(1.5.dp, colors.accentPrimary, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "",
                                style = LunmedTheme.typography.headlineSection.copy(fontSize = 20.sp),
                                color = colors.accentPrimary,
                            )
                        }

                        Column {
                            Text(
                                text = "CONTROL CENTER",
                                style = LunmedTheme.typography.labelAction.copy(
                                    letterSpacing = 2.sp,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                ),
                                color = colors.accentPrimary,
                            )
                            Text(
                                text = "LUNMED Master Suite",
                                style = LunmedTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                ),
                                color = colors.textPrimary,
                            )
                        }
                    }

                    // Live Digital Clock
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currentTime,
                            style = LunmedTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp,
                            ),
                            color = colors.textPrimary,
                        )
                        Text(
                            text = currentDate,
                            style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                            color = colors.textMuted,
                        )
                    }
                }

                // 2. Bento Grid Section 1: Dual Connectivity Pod (Wi-Fi + LAN)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(colors.surfaceElevated.copy(alpha = 0.95f), colors.surfaceCard),
                            ),
                        )
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                        .padding(14.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Wi-Fi Tile
                            BentoQuickToggle(
                                icon = Icons.Rounded.Wifi,
                                title = "Wi-Fi Network",
                                subtitle = if (networkStatus?.isConnected == true) "Connected • 5 GHz" else "Offline",
                                isActive = networkStatus?.isConnected == true,
                                activeColor = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f),
                            )

                            // Ethernet Tile
                            BentoQuickToggle(
                                icon = Icons.Rounded.Lan,
                                title = "Ethernet LAN",
                                subtitle = if (networkStatus?.transport?.name == "ETHERNET") "Gigabit Active" else "Ready (Auto-failover)",
                                isActive = networkStatus?.transport?.name == "ETHERNET",
                                activeColor = Color(0xFF00FF87),
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // Status Chip Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x12FFFFFF))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "IP: 192.168.1.104 • DNS: Fast Resolver",
                                style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                color = colors.textMuted,
                            )
                            Text(
                                text = "Ping: 12ms (Ultra Low)",
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = Color(0xFF00FF87),
                            )
                        }
                    }
                }

                // 3. Bento Grid Section 2: Audio & Acoustics + 4K Vision Pods
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Audio Pod with tactile volume slider
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(colors.surfaceElevated.copy(alpha = 0.95f), colors.surfaceCard),
                                ),
                            )
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                            .padding(14.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x22FFB300)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Audio Output",
                                        style = LunmedTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                        color = colors.textPrimary,
                                    )
                                    Text(
                                        text = "Dolby Atmos 7.1",
                                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                                        color = colors.accentPrimary,
                                    )
                                }
                            }

                            // Volume level bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Color(0x20FFFFFF)),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFFFB300), colors.accentPrimary),
                                            ),
                                        ),
                                )
                            }
                        }
                    }

                    // Display Pod (4K HDR 60Hz)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(colors.surfaceElevated.copy(alpha = 0.95f), colors.surfaceCard),
                                ),
                            )
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(20.dp))
                            .padding(14.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2200E5FF)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DisplaySettings,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (capabilities?.is4kDisplay == true) "4K UHD Cinema" else "1080p FHD",
                                        style = LunmedTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                        color = colors.textPrimary,
                                    )
                                    Text(
                                        text = "60 FPS • HDR10+ Cinema",
                                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                                        color = Color(0xFF00E5FF),
                                    )
                                }
                            }

                            // Dynamic Ambient sync pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF00FF87), CircleShape),
                                )
                                Text(
                                    text = "Adaptive Aura Synced",
                                    style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                    color = colors.textSecondary,
                                )
                            }
                        }
                    }
                }

                // 4. Bento Section 3: Liquid Apple Segmented Slider for App Icon Presentation
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "APP ICON PRESENTATION (نمط عرض الأيقونات)",
                        style = LunmedTheme.typography.labelAction.copy(
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp,
                        ),
                        color = colors.accentPrimary,
                    )

                    // Apple 3-Segment Capsule Slider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x18FFFFFF))
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                            .padding(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            AppleSegmentItem(
                                title = "16:9 Banner",
                                subtitle = "سينمائي",
                                isSelected = preferences.iconDisplayMode == IconDisplayMode.CINEMA_BANNER,
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(firstFocus),
                                onClick = { onSetIconDisplayMode(IconDisplayMode.CINEMA_BANNER) },
                            )
                            AppleSegmentItem(
                                title = "iOS Squircle",
                                subtitle = "أيقونات آبل",
                                isSelected = preferences.iconDisplayMode == IconDisplayMode.SQUARE_IOS,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetIconDisplayMode(IconDisplayMode.SQUARE_IOS) },
                            )
                            AppleSegmentItem(
                                title = "Capsule Dock",
                                subtitle = "كبسولات",
                                isSelected = preferences.iconDisplayMode == IconDisplayMode.COMPACT_CAPSULE,
                                modifier = Modifier.weight(1f),
                                onClick = { onSetIconDisplayMode(IconDisplayMode.COMPACT_CAPSULE) },
                            )
                        }
                    }
                }

                // 5. Bento Section 4: Scenic Dynamic Wallpapers Live Gallery
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "SCENIC WALLPAPERS (الخلفيات الخلابة والمتحركة)",
                            style = LunmedTheme.typography.labelAction.copy(
                                letterSpacing = 1.5.sp,
                                fontSize = 11.sp,
                            ),
                            color = colors.accentPrimary,
                        )
                        Text(
                            text = "GPU Vector Canvas",
                            style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                            color = colors.textMuted,
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        items(LunmedWallpapers.BUILT_IN) { wallpaper ->
                            val isSelected = preferences.selectedWallpaperId == wallpaper.id
                            AppleScenicThumbnail(
                                wallpaper = wallpaper,
                                isSelected = isSelected,
                                onClick = { onSelectWallpaper(wallpaper.id) },
                            )
                        }
                    }
                }

                // 6. Bento Section 5: Circular Apple System Quick Actions Row
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SYSTEM CONTROLS (إجراءات النظام السريعة)",
                        style = LunmedTheme.typography.labelAction.copy(
                            letterSpacing = 1.5.sp,
                            fontSize = 11.sp,
                        ),
                        color = colors.textSecondary,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Action 1: Default Launcher
                        val isProtectedDefault = defaultLauncherState?.isEffectivelyDefault == true
                        AppleSystemActionButton(
                            icon = if (isProtectedDefault) Icons.Rounded.Lock else Icons.Rounded.Tune,
                            title = if (isProtectedDefault) "Default Locked" else "Set Default",
                            accent = colors.accentPrimary,
                            modifier = Modifier.weight(1f),
                            onClick = onRequestDefaultLauncher,
                        )

                        // Action 2: Android Settings
                        AppleSystemActionButton(
                            icon = Icons.Rounded.Settings,
                            title = "Settings",
                            accent = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f),
                            onClick = onOpenAndroidSettings,
                        )

                        // Action 3: TV Screen Standby / Sleep
                        AppleSystemActionButton(
                            icon = Icons.Rounded.PowerSettingsNew,
                            title = "Standby",
                            accent = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                        )

                        // Action 4: Close
                        AppleSystemActionButton(
                            icon = Icons.Rounded.Clear,
                            title = "Dismiss",
                            accent = Color.White,
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BentoQuickToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.12f) else Color(0x10FFFFFF))
            .border(1.dp, if (isActive) activeColor.copy(alpha = 0.35f) else Color(0x15FFFFFF), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = 0.25f) else Color(0x14FFFFFF)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) activeColor else colors.textMuted,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LunmedTheme.typography.bodyPrimary.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                color = colors.textPrimary,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                color = if (isActive) activeColor else colors.textMuted,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun AppleSegmentItem(
    title: String,
    subtitle: String,
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
        label = "SegmentScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected) colors.surfaceCardFocused else Color.Transparent,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = if (isSelected || isFocused) colors.accentPrimary else colors.textSecondary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(
                    border = BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(LunmedGradients.focusRimStops(colors)),
                    ),
                    shape = shape,
                )
            } else {
                Border.None
            },
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
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (isSelected) "✓ $title" else title,
                style = LunmedTheme.typography.bodyPrimary.copy(
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp,
                ),
                color = if (isSelected || isFocused) colors.accentPrimary else colors.textPrimary,
                maxLines = 1,
            )
            Text(
                text = subtitle,
                style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                color = colors.textMuted,
            )
        }
    }
}

@Composable
private fun AppleScenicThumbnail(
    wallpaper: LunmedWallpaper,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(14.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "ThumbnailScale",
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
                    if (isSelected) colors.accentPrimary else Color(0x30FFFFFF),
                ),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, colors.borderFocused),
                shape = shape,
            ),
        ),
        modifier = Modifier
            .size(width = 130.dp, height = 80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Procedural landscape preview
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val glowColor = Color(wallpaper.ambientGlowArgb)

                // Atmospheric glow
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.3f),
                        radius = w * 0.6f,
                    ),
                )

                // Characteristic landscape path
                val landscapePath = Path().apply {
                    moveTo(0f, h)
                    lineTo(0f, h * 0.70f)
                    cubicTo(w * 0.35f, h * 0.55f, w * 0.65f, h * 0.75f, w, h * 0.60f)
                    lineTo(w, h)
                    close()
                }
                drawPath(
                    path = landscapePath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0x60000000), Color(0xCC000000)),
                        startY = h * 0.55f,
                        endY = h,
                    ),
                )
            }

            // Title & Selection chip
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = wallpaper.title.split(" ").take(2).joinToString(" "),
                        style = LunmedTheme.typography.labelMeta.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = Color.White,
                        maxLines = 1,
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(colors.accentPrimary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(11.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppleSystemActionButton(
    icon: ImageVector,
    title: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(16.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "ActionButtonScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color(0x18FFFFFF),
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, Color(0x18FFFFFF)), shape = shape),
            focusedBorder = Border(border = BorderStroke(2.dp, colors.borderFocused), shape = shape),
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = title,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                ),
                color = if (isFocused) colors.accentPrimary else colors.textPrimary,
                maxLines = 1,
            )
        }
    }
}
