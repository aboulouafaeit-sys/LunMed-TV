package com.lunmed.tv.feature.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
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
import com.lunmed.tv.core.design.tokens.LunmedGradients
import com.lunmed.tv.core.design.tokens.LunmedColors
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.IconDisplayMode
import com.lunmed.tv.core.model.InterfaceDensity
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.model.LunmedWallpapers
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.core.ui.R as UiR
import com.lunmed.tv.core.ui.components.AppleControlCenterOverlay
import com.lunmed.tv.core.ui.components.DefaultLauncherSetupDialog
import com.lunmed.tv.feature.settings.model.SettingsSection

@Composable
fun SettingsDetailPane(
    section: SettingsSection,
    preferences: LauncherPreferences,
    capabilities: DeviceCapabilities?,
    defaultLauncherState: DefaultLauncherState,
    networkStatus: NetworkStatus? = null,
    rootState: com.lunmed.tv.core.model.RootState = com.lunmed.tv.core.model.RootState(),
    onSetAccentPreset: (AccentPreset) -> Unit,
    onSetDensity: (InterfaceDensity) -> Unit,
    onSetCardSize: (CardSizePreset) -> Unit,
    onSetIconDisplayMode: (IconDisplayMode) -> Unit = {},
    onSelectWallpaper: (String) -> Unit = {},
    onSetAnimationIntensity: (AnimationIntensity) -> Unit,
    onToggleShowClock: (Boolean) -> Unit,
    onToggleShowDeviceIndicators: (Boolean) -> Unit,
    onToggleShowRecentApps: (Boolean) -> Unit,
    onToggleShowStartupSplash: (Boolean) -> Unit = {},
    onTogglePromptDefaultLauncherOnStartup: (Boolean) -> Unit = {},
    onRequestDefaultLauncher: () -> Unit,
    onRequestDeviceAdmin: () -> Unit = {},
    onOpenAndroidSettings: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onEnforceRootDefault: () -> Unit = {},
    onRevertRootDefault: () -> Unit = {},
    onSetLanguageTag: (String) -> Unit,
    onOpenResetConfirmation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors
    var showAppleControlCenter by remember { mutableStateOf(false) }
    var showLauncherSetupFromControlCenter by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .lunmedFocusGroupRestorer()
            .padding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        Column {
            Text(
                text = section.title,
                style = LunmedTheme.typography.headlineSection,
                color = colors.textPrimary,
            )
            Text(
                text = section.subtitle,
                style = LunmedTheme.typography.bodySecondary,
                color = colors.textMuted,
            )
        }

        when (section) {
            SettingsSection.APPEARANCE -> {
                // 1. Apple tvOS Control Center Hero Card
                AppearanceHeroControlCenterBanner(
                    onClick = { showAppleControlCenter = true },
                )

                // 2. Signature Accent Palette Swatch Gallery
                SettingGroup(title = "Signature Accent Palette (ألوان النظام الملكية)") {
                    PaletteSwatchGallery(
                        selectedPreset = preferences.accentPreset,
                        onSelectPreset = onSetAccentPreset,
                    )
                }

                // 3. App Icon Presentation Visual Gallery
                SettingGroup(title = "App Icon Presentation Mode (طريقة عرض الأيقونات)") {
                    IconModeVisualSelector(
                        selectedMode = preferences.iconDisplayMode,
                        onSelectMode = onSetIconDisplayMode,
                    )
                }

                // 4. Scenic Dynamic Wallpapers Showcase Gallery
                SettingGroup(title = "Scenic Dynamic Wallpapers (الخلفيات الخلابة والمتحركة)") {
                    ScenicWallpaperShowcase(
                        selectedWallpaperId = preferences.selectedWallpaperId,
                        onSelectWallpaper = onSelectWallpaper,
                    )
                }

                // 5. Interface Density
                SettingGroup(title = "Interface Density (كثافة وهوامش الشاشة)") {
                    InterfaceDensity.entries.forEach { density ->
                        val (title, sub) = when (density) {
                            InterfaceDensity.COMFORTABLE -> "Comfortable (مريح)" to "Spacious margins (48dp) for larger living room displays"
                            InterfaceDensity.COMPACT -> "Compact (مضغوط)" to "Tighter spacing (40dp) to maximize visible apps on screen"
                        }
                        SelectableOptionRow(
                            title = title,
                            subtitle = sub,
                            isSelected = preferences.density == density,
                            onClick = { onSetDensity(density) },
                        )
                    }
                }

                // 6. App Card Dimensions
                SettingGroup(title = "App Card Dimensions (أبعاد بطاقات التطبيقات)") {
                    CardSizePreset.entries.forEach { size ->
                        val (title, sub) = when (size) {
                            CardSizePreset.COMPACT -> "Compact (160×90)" to "High density layout"
                            CardSizePreset.STANDARD -> "Standard (192×108)" to "Balanced 16:9 TV card size"
                            CardSizePreset.CINEMA -> "Cinema (224×126)" to "Larger artwork and prominent focus"
                        }
                        SelectableOptionRow(
                            title = title,
                            subtitle = sub,
                            isSelected = preferences.cardSize == size,
                            onClick = { onSetCardSize(size) },
                        )
                    }
                }
            }

            SettingsSection.ANIMATIONS -> {
                SettingGroup(title = "Motion & Transition Engine") {
                    AnimationIntensity.entries.forEach { intensity ->
                        val (title, sub) = when (intensity) {
                            AnimationIntensity.FULL -> "Full Animations" to "Smooth 150ms deceleration curves for fluid TV experience"
                            AnimationIntensity.REDUCED -> "Reduced Motion" to "Fast 90ms transitions for snappy budget TV box responsiveness"
                            AnimationIntensity.MINIMAL -> "Minimal / Instant" to "Immediate 0ms state changes with zero GPU animation load"
                        }
                        SelectableOptionRow(
                            title = title,
                            subtitle = sub,
                            isSelected = preferences.animationIntensity == intensity,
                            onClick = { onSetAnimationIntensity(intensity) },
                        )
                    }
                }

                if (capabilities?.isLowRamDevice == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceElevated, LunmedTheme.shapes.cardSmall)
                            .padding(14.dp),
                    ) {
                        Text(
                            text = "⚡ Hardware Optimization Active: Low-RAM device detected. Reduced animations are automatically applied to preserve system memory.",
                            style = LunmedTheme.typography.bodySecondary,
                            color = colors.statusWarning,
                        )
                    }
                }
            }

            SettingsSection.HOME_SCREEN -> {
                SettingGroup(title = "Home Screen Elements") {
                    ToggleOptionRow(
                        title = "Clock & Date Header",
                        subtitle = "Display live time and current date in the top bar",
                        isChecked = preferences.showClock,
                        onToggle = onToggleShowClock,
                    )
                    ToggleOptionRow(
                        title = "Device Status Indicators",
                        subtitle = "Show Wi-Fi, Ethernet, and 4K display badges",
                        isChecked = preferences.showDeviceIndicators,
                        onToggle = onToggleShowDeviceIndicators,
                    )
                    ToggleOptionRow(
                        title = "Recently Used Applications",
                        subtitle = "Show an auto-updating rail of recently launched apps",
                        isChecked = preferences.showRecentApps,
                        onToggle = onToggleShowRecentApps,
                    )
                    ToggleOptionRow(
                        title = "Startup Intro Animation",
                        subtitle = "Display a brief obsidian amber cinematic reveal on cold boot (dismissable with any key)",
                        isChecked = preferences.showStartupSplash,
                        onToggle = onToggleShowStartupSplash,
                    )
                }
            }

            SettingsSection.DEFAULT_LAUNCHER -> {
                SettingGroup(title = "Root Level Factory Lockdown (الجذر)") {
                    val rootStatusText = if (rootState.isRootAvailable) {
                        "Root (su) detected: ${rootState.suBinaryPath ?: "Active"}. Direct kernel lockdown available."
                    } else {
                        "Root not detected. Standard Android or Accessibility Watchdog mode is active."
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (rootState.isRootAvailable) colors.surfaceCardFocused else colors.surfaceElevated,
                                LunmedTheme.shapes.cardSmall,
                            )
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                text = "Root Engine Status",
                                style = LunmedTheme.typography.labelAction,
                                color = if (rootState.isRootAvailable) colors.accentPrimary else colors.textMuted,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rootStatusText,
                                style = LunmedTheme.typography.bodySecondary,
                                color = colors.textSecondary,
                            )
                        }
                    }

                    if (rootState.isRootAvailable) {
                        ActionSettingButton(
                            title = "Enforce LUNMED TV at Root Level (تثبيت كـ مشغل النظام بالجذر)",
                            subtitle = "Directly locks Home activity in AOSP and disables competing vendor OEM launchers",
                            icon = Icons.Rounded.Tune,
                            onClick = onEnforceRootDefault,
                        )

                        ActionSettingButton(
                            title = "Revert Root Lockdown (إلغاء حظر مشغلات OEM)",
                            subtitle = "Re-enables vendor stock launchers in the package manager",
                            icon = Icons.Rounded.RestartAlt,
                            onClick = onRevertRootDefault,
                        )
                    }
                }

                SettingGroup(title = "Boot Persistence Layers (طبقات التثبيت بعد إعادة التشغيل)") {
                    InfoSettingRow(
                        title = "Android Home Role",
                        value = when {
                            defaultLauncherState.isDefaultLauncher -> "Held by LUNMED TV"
                            defaultLauncherState.currentDefaultPackage != null ->
                                "Held by ${defaultLauncherState.currentDefaultPackage}"
                            else -> "Not assigned"
                        },
                    )
                    InfoSettingRow(
                        title = "Accessibility Watchdog",
                        value = if (defaultLauncherState.isWatchdogEnabled) {
                            "Enabled (Home key + vendor launcher interception)"
                        } else {
                            "Not enabled (open Accessibility settings below)"
                        },
                    )
                    InfoSettingRow(
                        title = "Device Administrator",
                        value = if (defaultLauncherState.isDeviceAdminActive) {
                            "Active"
                        } else {
                            "Inactive"
                        },
                    )
                    InfoSettingRow(
                        title = "Device Owner Lockdown",
                        value = if (defaultLauncherState.isDeviceOwner) {
                            "Active (immutable ROM-level HOME)"
                        } else {
                            "Not provisioned (requires ADB provisioning)"
                        },
                    )

                    if (!defaultLauncherState.isDeviceAdminActive &&
                        !defaultLauncherState.isDeviceOwner
                    ) {
                        ActionSettingButton(
                            title = "Activate Device Administrator (تفعيل مدير الجهاز)",
                            subtitle = "Lets LUNMED TV re-assert HOME after reboots on ROMs that reset the default launcher",
                            icon = Icons.Rounded.Tune,
                            onClick = onRequestDeviceAdmin,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceElevated.copy(alpha = 0.5f), LunmedTheme.shapes.cardSmall)
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                text = "Fully immutable launcher (STB / reseller provisioning):",
                                style = LunmedTheme.typography.labelAction,
                                color = colors.accentPrimary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "adb shell dpm set-device-owner com.lunmed.tv/com.lunmed.tv.receiver.LunmedDeviceAdminReceiver",
                                style = LunmedTheme.typography.labelMeta,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }

                SettingGroup(title = "Android Home Role") {
                    val statusText = if (defaultLauncherState.isDefaultLauncher) {
                        "LUNMED TV is currently set as your default launcher."
                    } else {
                        "Another launcher is active (${defaultLauncherState.currentDefaultPackage ?: "None"})."
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceElevated, LunmedTheme.shapes.cardSmall)
                            .padding(14.dp),
                    ) {
                        Text(
                            text = statusText,
                            style = LunmedTheme.typography.bodyPrimary,
                            color = if (defaultLauncherState.isDefaultLauncher) colors.statusSuccess else colors.statusWarning,
                        )
                    }

                    if (!defaultLauncherState.isDefaultLauncher && defaultLauncherState.canRequestRole) {
                        ActionSettingButton(
                            title = "Set LUNMED TV as Default Launcher",
                            subtitle = "Prompts the official Android home role selector",
                            icon = Icons.Rounded.Tune,
                            onClick = onRequestDefaultLauncher,
                        )
                    }

                    ActionSettingButton(
                        title = stringResource(UiR.string.settings_watchdog_persistent),
                        subtitle = stringResource(UiR.string.settings_watchdog_persistent_sub),
                        icon = Icons.Rounded.Tune,
                        highlight = !defaultLauncherState.isEffectivelyDefault,
                        onClick = onOpenAccessibilitySettings,
                    )

                    ToggleOptionRow(
                        title = stringResource(UiR.string.settings_prompt_startup_title),
                        subtitle = stringResource(UiR.string.settings_prompt_startup_sub),
                        isChecked = preferences.promptDefaultLauncherOnStartup,
                        onToggle = onTogglePromptDefaultLauncherOnStartup,
                    )

                    ActionSettingButton(
                        title = "Open Android System Settings",
                        subtitle = "Access Wi-Fi, Bluetooth, Audio, and Display settings",
                        icon = Icons.Rounded.Tune,
                        onClick = onOpenAndroidSettings,
                    )

                    ActionSettingButton(
                        title = "Reset All Preferences to Defaults",
                        subtitle = "Restores factory theme, layout, and animation settings",
                        icon = Icons.Rounded.RestartAlt,
                        highlight = true,
                        onClick = onOpenResetConfirmation,
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceElevated.copy(alpha = 0.5f), LunmedTheme.shapes.cardSmall)
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                text = "STB / Reseller Permanent Lock Command (ADB):",
                                style = LunmedTheme.typography.labelAction,
                                color = colors.accentPrimary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "adb shell cmd role add-role-holder android.app.role.HOME com.lunmed.tv",
                                style = LunmedTheme.typography.labelMeta,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }
            }

            SettingsSection.LANGUAGE -> {
                SettingGroup(title = "Interface Language & Direction") {
                    listOf(
                        "system" to ("System Default" to "Follow host TV operating system language"),
                        "ar" to ("العربية (Arabic)" to "واجهة عربية أصيلة مع دعم كامل للاتجاه من اليمين لليسار (RTL)"),
                        "en" to ("English" to "Standard international English language"),
                        "fr" to ("Français (French)" to "Interface en français avec typographie adaptée"),
                        "es" to ("Español (Spanish)" to "Interfaz en español optimizada para TV"),
                        "it" to ("Italiano (Italian)" to "Interfaccia in italiano"),
                    ).forEach { (tag, info) ->
                        val isSelected = preferences.languageTag == tag
                        ActionSettingButton(
                            title = info.first,
                            subtitle = info.second,
                            icon = if (isSelected) Icons.Rounded.Check else Icons.Rounded.Language,
                            highlight = isSelected,
                            onClick = { onSetLanguageTag(tag) },
                        )
                    }
                }

                SettingGroup(title = "Accessibility & TV Ergonomics") {
                    ActionSettingButton(
                        title = "Open Android Accessibility Center",
                        subtitle = "Configure TalkBack, high-contrast text, captions, and screen magnification",
                        icon = Icons.Rounded.Tune,
                        onClick = onOpenAccessibilitySettings,
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceElevated.copy(alpha = 0.5f), LunmedTheme.shapes.cardSmall)
                            .padding(14.dp),
                    ) {
                        Column {
                            Text(
                                text = "10-Foot Television Contrast & Readability:",
                                style = LunmedTheme.typography.labelAction,
                                color = colors.accentPrimary,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "All text sizes and focus rings in LUNMED TV exceed WCAG AAA requirements with a minimum 7:1 contrast ratio against obsidian dark backdrops for effortless viewing at 3+ meters.",
                                style = LunmedTheme.typography.labelMeta,
                                color = colors.textSecondary,
                            )
                        }
                    }
                }
            }

            SettingsSection.ABOUT -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.md),
                    contentAlignment = Alignment.Center,
                ) {
                    com.lunmed.tv.core.ui.components.LunmedBrandBadge(
                        size = com.lunmed.tv.core.ui.components.LunmedBrandBadgeSize.MEDIUM,
                        showWordmark = true,
                        animatedSheen = true,
                    )
                }

                SettingGroup(title = "Application Information") {
                    InfoSettingRow(title = "Application Name", value = "LUNMED TV")
                    InfoSettingRow(title = "Version", value = "0.1.0-alpha (Milestone 06)")
                    InfoSettingRow(title = "Product Category", value = "Premium Android TV Launcher")
                }

                SettingGroup(title = "Device & Hardware Capabilities") {
                    InfoSettingRow(title = "Android API Level", value = "API ${capabilities?.sdkInt ?: "Unknown"}")
                    InfoSettingRow(
                        title = "Screen Resolution",
                        value = "${capabilities?.screenWidthPx ?: 0} × ${capabilities?.screenHeightPx ?: 0} (${capabilities?.densityDpi ?: 0} DPI)",
                    )
                    InfoSettingRow(
                        title = "4K Display Detected",
                        value = if (capabilities?.is4kDisplay == true) "Yes (3840×2160+)" else "Standard 1080p / 720p",
                    )
                    InfoSettingRow(
                        title = "Leanback System Feature",
                        value = if (capabilities?.isLeanbackSupported == true) "Supported" else "Not reported (AOSP TV Box)",
                    )
                    InfoSettingRow(
                        title = "Low-RAM Device",
                        value = if (capabilities?.isLowRamDevice == true) "Yes (Memory-conserving mode active)" else "No",
                    )
                }
            }
        }
    }

    if (showAppleControlCenter) {
        AppleControlCenterOverlay(
            preferences = preferences,
            networkStatus = networkStatus,
            capabilities = capabilities,
            defaultLauncherState = defaultLauncherState,
            onSetIconDisplayMode = onSetIconDisplayMode,
            onSelectWallpaper = onSelectWallpaper,
            onOpenAndroidSettings = onOpenAndroidSettings,
            onRequestDefaultLauncher = {
                showAppleControlCenter = false
                showLauncherSetupFromControlCenter = true
            },
            onDismiss = { showAppleControlCenter = false },
        )
    }

    if (showLauncherSetupFromControlCenter) {
        DefaultLauncherSetupDialog(
            defaultLauncherState = defaultLauncherState,
            isStartupPrompt = false,
            onEnableAccessibilityWatchdog = onOpenAccessibilitySettings,
            onRequestSystemHomeRole = onRequestDefaultLauncher,
            onRequestDeviceAdmin = onRequestDeviceAdmin,
            onDismiss = { showLauncherSetupFromControlCenter = false },
        )
    }
}

@Composable
private fun AppearanceHeroControlCenterBanner(
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val shape = RoundedCornerShape(24.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        animationSpec = anim.scaleSpringSpec(),
        label = "HeroBannerScale",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(
                        listOf(colors.accentPrimary.copy(alpha = 0.5f), Color(0x22FFFFFF), Color(0x0EFFFFFF)),
                    ),
                ),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(
                    2.dp,
                    Brush.linearGradient(LunmedGradients.focusRimStops(colors)),
                ),
                shape = shape,
            ),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        if (isFocused) colors.surfaceCardFocused else colors.surfaceElevated.copy(alpha = 0.95f),
                        colors.surfaceCard.copy(alpha = 0.92f),
                        colors.obsidianCanvas,
                    ),
                ),
                shape,
            )
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(colors.accentPrimary.copy(alpha = 0.35f), Color(0x18FFFFFF)),
                        ),
                    )
                    .border(1.5.dp, colors.accentPrimary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "",
                    style = LunmedTheme.typography.headlineSection.copy(fontSize = 24.sp),
                    color = colors.accentPrimary,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Apple tvOS Control Center",
                        style = LunmedTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = colors.textPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .background(colors.accentPrimary.copy(alpha = 0.20f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "tvOS 18 Luxury",
                            style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = colors.accentPrimary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "افتح واجهة تحكم آبل العائمة السريعة للاتصال، الصوت، دقة العرض 4K، والخلفيات الخلابة بنقرة واحدة",
                    style = LunmedTheme.typography.bodySecondary.copy(fontSize = 12.sp),
                    color = colors.textMuted,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFocused) colors.accentPrimary else Color(0x22FFFFFF))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "فتح المركز ▷",
                    style = LunmedTheme.typography.labelAction.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isFocused) Color.Black else colors.textPrimary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PaletteSwatchGallery(
    selectedPreset: AccentPreset,
    onSelectPreset: (AccentPreset) -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AccentPreset.entries.forEach { preset ->
            val isSelected = preset == selectedPreset
            val tokens = LunmedColors.forAccent(preset)
            val name = when (preset) {
                AccentPreset.CHAMPAGNE_GOLD -> "Champagne Gold"
                AccentPreset.ICE_CYAN -> "Ice Cyan"
                AccentPreset.PLATINUM_SILVER -> "Platinum Silver"
                AccentPreset.OBSIDIAN_AMBER -> "Obsidian Amber"
            }
            val subtitle = when (preset) {
                AccentPreset.CHAMPAGNE_GOLD -> "ذهب عيار 24"
                AccentPreset.ICE_CYAN -> "سيان جليدي"
                AccentPreset.PLATINUM_SILVER -> "بلاتين ملكي"
                AccentPreset.OBSIDIAN_AMBER -> "عنبر أوبسيديان"
            }

            var isFocused by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isFocused) 1.05f else 1.0f,
                animationSpec = anim.scaleSpringSpec(),
                label = "SwatchScale",
            )

            val shape = RoundedCornerShape(16.dp)

            Surface(
                onClick = { onSelectPreset(preset) },
                shape = ClickableSurfaceDefaults.shape(shape),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = if (isSelected) colors.surfaceCardFocused else colors.surfaceElevated,
                    focusedContainerColor = colors.surfaceCardFocused,
                    contentColor = colors.textPrimary,
                    focusedContentColor = tokens.accentPrimary,
                ),
                border = ClickableSurfaceDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) tokens.accentPrimary else Color(0x20FFFFFF),
                        ),
                        shape = shape,
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(2.dp, colors.borderFocused),
                        shape = shape,
                    ),
                ),
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(shape)
                    .onFocusChanged { isFocused = it.isFocused },
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        tokens.accentPrimary,
                                        tokens.accentSecondary,
                                        tokens.accentPrimary.copy(alpha = 0.8f),
                                    ),
                                ),
                            ),
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.75f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = tokens.accentPrimary,
                                    modifier = Modifier.size(12.dp),
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = name,
                            style = LunmedTheme.typography.bodyPrimary.copy(
                                fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                            ),
                            color = if (isSelected || isFocused) tokens.accentPrimary else colors.textPrimary,
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
        }
    }
}

@Composable
private fun IconModeVisualSelector(
    selectedMode: IconDisplayMode,
    onSelectMode: (IconDisplayMode) -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconDisplayMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            var isFocused by remember { mutableStateOf(false) }

            val scale by animateFloatAsState(
                targetValue = if (isFocused) 1.04f else 1.0f,
                animationSpec = anim.scaleSpringSpec(),
                label = "IconModeScale",
            )

            val shape = RoundedCornerShape(18.dp)

            val (title, subtitle) = when (mode) {
                IconDisplayMode.CINEMA_BANNER -> "Cinema Banners" to "16:9 أشرطة سينمائية عريضة"
                IconDisplayMode.SQUARE_IOS -> "Apple TV Squircle" to "1:1 أيقونات آبل المربعة الفاخرة"
                IconDisplayMode.COMPACT_CAPSULE -> "Capsule Dock" to "كبسولات أفقية عصرية مضغوطة"
            }

            Surface(
                onClick = { onSelectMode(mode) },
                shape = ClickableSurfaceDefaults.shape(shape),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = if (isSelected) colors.surfaceCardFocused else colors.surfaceElevated,
                    focusedContainerColor = colors.surfaceCardFocused,
                    contentColor = colors.textPrimary,
                    focusedContentColor = colors.accentPrimary,
                ),
                border = ClickableSurfaceDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) colors.accentPrimary else Color(0x20FFFFFF),
                        ),
                        shape = shape,
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(2.dp, colors.borderFocused),
                        shape = shape,
                    ),
                ),
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(shape)
                    .onFocusChanged { isFocused = it.isFocused },
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x15FFFFFF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        when (mode) {
                            IconDisplayMode.CINEMA_BANNER -> {
                                Box(
                                    modifier = Modifier
                                        .size(width = 96.dp, height = 52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(colors.accentPrimary.copy(alpha = 0.35f), Color(0x33FFFFFF)),
                                            ),
                                        )
                                        .border(1.dp, if (isSelected) colors.accentPrimary else Color(0x33FFFFFF), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Tv,
                                        contentDescription = null,
                                        tint = if (isSelected) colors.accentPrimary else Color.White,
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                            IconDisplayMode.SQUARE_IOS -> {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(colors.accentPrimary.copy(alpha = 0.40f), Color(0x22FFFFFF)),
                                            ),
                                        )
                                        .border(1.2.dp, if (isSelected) colors.accentPrimary else Color(0x44FFFFFF), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "",
                                        style = LunmedTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                        color = if (isSelected) colors.accentPrimary else Color.White,
                                    )
                                }
                            }
                            IconDisplayMode.COMPACT_CAPSULE -> {
                                Row(
                                    modifier = Modifier
                                        .size(width = 110.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(colors.accentPrimary.copy(alpha = 0.25f), Color(0x22FFFFFF)),
                                            ),
                                        )
                                        .border(1.dp, if (isSelected) colors.accentPrimary else Color(0x33FFFFFF), RoundedCornerShape(18.dp))
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(colors.accentPrimary),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(width = 50.dp, height = 6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.White.copy(alpha = 0.6f)),
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = title,
                                style = LunmedTheme.typography.bodyPrimary.copy(
                                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                ),
                                color = if (isSelected || isFocused) colors.accentPrimary else colors.textPrimary,
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(colors.accentPrimary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(10.dp),
                                    )
                                }
                            }
                        }
                        Text(
                            text = subtitle,
                            style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                            color = colors.textMuted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScenicWallpaperShowcase(
    selectedWallpaperId: String,
    onSelectWallpaper: (String) -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(LunmedWallpapers.BUILT_IN) { wallpaper ->
            val isSelected = wallpaper.id == selectedWallpaperId
            var isFocused by remember { mutableStateOf(false) }

            val scale by animateFloatAsState(
                targetValue = if (isFocused) 1.06f else 1.0f,
                animationSpec = anim.scaleSpringSpec(),
                label = "WallpaperShowcaseScale",
            )

            val shape = RoundedCornerShape(18.dp)

            Surface(
                onClick = { onSelectWallpaper(wallpaper.id) },
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
                            if (isSelected) 2.2.dp else 1.dp,
                            if (isSelected) colors.accentPrimary else Color(0x30FFFFFF),
                        ),
                        shape = shape,
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(2.2.dp, colors.borderFocused),
                        shape = shape,
                    ),
                ),
                modifier = Modifier
                    .size(width = 170.dp, height = 110.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(shape)
                    .onFocusChanged { isFocused = it.isFocused },
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val glow = Color(wallpaper.ambientGlowArgb)

                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(glow.copy(alpha = 0.50f), Color.Transparent),
                                center = Offset(w * 0.5f, h * 0.25f),
                                radius = w * 0.65f,
                            ),
                        )

                        val path = Path().apply {
                            moveTo(0f, h)
                            lineTo(0f, h * 0.72f)
                            cubicTo(w * 0.35f, h * 0.58f, w * 0.65f, h * 0.80f, w, h * 0.65f)
                            lineTo(w, h)
                            close()
                        }
                        drawPath(
                            path = path,
                            brush = Brush.verticalGradient(
                                listOf(Color(0x55000000), Color(0xDD000000)),
                                startY = h * 0.58f,
                                endY = h,
                            ),
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(colors.accentPrimary),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(13.dp),
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = wallpaper.title,
                                style = LunmedTheme.typography.bodyPrimary.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                ),
                                color = if (isSelected) colors.accentPrimary else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = wallpaper.scenicType.name.replace("_", " "),
                                style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
        Text(
            text = title.uppercase(),
            style = LunmedTheme.typography.labelMeta,
            color = colors.textMuted,
            modifier = Modifier.padding(start = 4.dp),
        )
        content()
    }
}

@Composable
private fun SelectableOptionRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    leadingChipColor: Color? = null,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val shape = LunmedTheme.shapes.cardSmall
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            isSelected -> colors.surfaceElevated
            else -> colors.surfaceCard
        },
        animationSpec = anim.focusSpec(),
        label = "SelectableOptionBg",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.textPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(1.dp, colors.accentPrimary.copy(alpha = 0.5f)), shape = shape)
            } else {
                Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = shape)
            },
            focusedBorder = Border(
                border = BorderStroke(2.dp, colors.borderFocused),
                shape = shape,
            ),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (leadingChipColor != null) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(leadingChipColor, LunmedTheme.shapes.pill),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = title,
                        style = LunmedTheme.typography.bodyPrimary,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = subtitle,
                        style = LunmedTheme.typography.labelMeta,
                        color = colors.textMuted,
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "Selected",
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun ToggleOptionRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val shape = LunmedTheme.shapes.cardSmall
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            else -> colors.surfaceCard
        },
        animationSpec = anim.focusSpec(),
        label = "ToggleOptionBg",
    )

    Surface(
        onClick = { onToggle(!isChecked) },
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.textPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = shape),
            focusedBorder = Border(border = BorderStroke(2.dp, colors.borderFocused), shape = shape),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary,
                    color = colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                )
            }

            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(24.dp)
                    .background(
                        color = if (isChecked) colors.accentPrimary.copy(alpha = 0.25f) else colors.surfaceElevated,
                        shape = LunmedTheme.shapes.pill,
                    )
                    .border(
                        width = 1.dp,
                        color = if (isChecked) colors.accentPrimary else colors.borderSubtle,
                        shape = LunmedTheme.shapes.pill,
                    )
                    .padding(3.dp),
                contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            color = if (isChecked) colors.accentPrimary else colors.textMuted,
                            shape = androidx.compose.foundation.shape.CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun ActionSettingButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    highlight: Boolean = false,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val shape = LunmedTheme.shapes.cardSmall
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            highlight -> colors.statusError.copy(alpha = 0.08f)
            else -> colors.surfaceCard
        },
        animationSpec = anim.focusSpec(),
        label = "ActionButtonBg",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = if (highlight) colors.statusError else colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = shape),
            focusedBorder = Border(
                border = BorderStroke(2.dp, if (highlight) colors.statusError else colors.borderFocused),
                shape = shape,
            ),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (highlight) colors.statusError else colors.accentPrimary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary,
                    color = if (highlight) colors.statusError else colors.textPrimary,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun InfoSettingRow(
    title: String,
    value: String,
) {
    val colors = LunmedTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceElevated.copy(alpha = 0.5f), LunmedTheme.shapes.cardSmall)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = LunmedTheme.typography.bodySecondary,
            color = colors.textSecondary,
        )
        Text(
            text = value,
            style = LunmedTheme.typography.labelAction,
            color = colors.textPrimary,
        )
    }
}

@Composable
fun ResetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(420.dp)
                .background(colors.surfaceElevated, LunmedTheme.shapes.panel)
                .padding(spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                Text(
                    text = "Reset All Preferences?",
                    style = LunmedTheme.typography.headlineSection,
                    color = colors.textPrimary,
                )
                Text(
                    text = "This will restore the Champagne Gold theme, comfortable density, standard card sizes, and full animation effects.",
                    style = LunmedTheme.typography.bodySecondary,
                    color = colors.textSecondary,
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Surface(
                        onClick = onConfirm,
                        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = colors.statusError.copy(alpha = 0.2f),
                            focusedContainerColor = colors.statusError,
                            contentColor = colors.statusError,
                            focusedContentColor = colors.obsidianCanvas,
                        ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Reset to Defaults",
                            style = LunmedTheme.typography.labelAction,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        )
                    }

                    Surface(
                        onClick = onDismiss,
                        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = colors.surfaceCard,
                            focusedContainerColor = colors.surfaceCardFocused,
                            contentColor = colors.textPrimary,
                            focusedContentColor = colors.accentPrimary,
                        ),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "Cancel",
                            style = LunmedTheme.typography.labelAction,
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

