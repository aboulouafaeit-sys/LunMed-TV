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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tune
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.design.tokens.LunmedGradients
import com.lunmed.tv.core.model.DefaultLauncherState
import com.lunmed.tv.core.ui.R

/**
 * Projectivy-inspired onboarding and activation dialog for making LUNMED TV the
 * persistent default TV launcher via Android's Accessibility Watchdog, System Home Role,
 * and optional Device Administrator fallback.
 */
@Composable
fun DefaultLauncherSetupDialog(
    defaultLauncherState: DefaultLauncherState,
    isStartupPrompt: Boolean = false,
    onEnableAccessibilityWatchdog: () -> Unit,
    onRequestSystemHomeRole: () -> Unit,
    onRequestDeviceAdmin: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    onDontAskAgain: (() -> Unit)? = null,
) {
    val colors = LunmedTheme.colors
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        runCatching { firstFocus.requestFocus() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .width(600.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.surfaceElevated.copy(alpha = 0.97f),
                            colors.surfaceCard.copy(alpha = 0.99f),
                            colors.obsidianCanvas,
                        ),
                    ),
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.65f),
                                colors.borderSubtle,
                                colors.borderGlass,
                            ),
                        ),
                    ),
                    RoundedCornerShape(24.dp),
                )
                .padding(26.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        colors.accentPrimary.copy(alpha = 0.35f),
                                        colors.surfaceCard,
                                    ),
                                ),
                            )
                            .border(1.dp, colors.accentPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.launcher_setup_title),
                            style = LunmedTheme.typography.headlineSection.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.2.sp,
                            ),
                            color = colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.launcher_setup_subtitle),
                            style = LunmedTheme.typography.bodySecondary,
                            color = colors.textSecondary,
                        )
                    }
                }

                // Hairline Divider
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

                // Tier 1 (Primary Recommended): Projectivy-style Accessibility Watchdog
                LauncherSetupOptionCard(
                    icon = Icons.Rounded.Tune,
                    title = stringResource(R.string.launcher_setup_accessibility_title),
                    subtitle = stringResource(R.string.launcher_setup_accessibility_sub),
                    helperHint = if (!defaultLauncherState.isWatchdogEnabled) {
                        stringResource(R.string.launcher_setup_accessibility_guide)
                    } else {
                        null
                    },
                    badgeText = if (defaultLauncherState.isWatchdogEnabled) {
                        stringResource(R.string.launcher_setup_badge_active)
                    } else {
                        stringResource(R.string.launcher_setup_badge_recommended)
                    },
                    badgeSuccess = defaultLauncherState.isWatchdogEnabled,
                    isPrimary = true,
                    onClick = onEnableAccessibilityWatchdog,
                    modifier = Modifier.focusRequester(firstFocus),
                )

                // Tier 2: Official Android System HOME Role (when available on ROM)
                if (defaultLauncherState.canRequestRole || defaultLauncherState.isDefaultLauncher) {
                    LauncherSetupOptionCard(
                        icon = Icons.Rounded.Settings,
                        title = stringResource(R.string.launcher_setup_role_title),
                        subtitle = if (defaultLauncherState.isDefaultLauncher) {
                            stringResource(R.string.launcher_setup_role_held)
                        } else {
                            stringResource(R.string.launcher_setup_role_sub)
                        },
                        helperHint = null,
                        badgeText = if (defaultLauncherState.isDefaultLauncher) {
                            stringResource(R.string.launcher_setup_badge_active)
                        } else {
                            stringResource(R.string.launcher_setup_badge_inactive)
                        },
                        badgeSuccess = defaultLauncherState.isDefaultLauncher,
                        isPrimary = false,
                        onClick = onRequestSystemHomeRole,
                    )
                }

                // Tier 3: Optional Device Administrator Fallback
                if (onRequestDeviceAdmin != null &&
                    !defaultLauncherState.isDeviceAdminActive &&
                    !defaultLauncherState.isDeviceOwner
                ) {
                    LauncherSetupOptionCard(
                        icon = Icons.Rounded.Lock,
                        title = stringResource(R.string.launcher_setup_admin_title),
                        subtitle = stringResource(R.string.launcher_setup_admin_sub),
                        helperHint = null,
                        badgeText = null,
                        badgeSuccess = false,
                        isPrimary = false,
                        onClick = onRequestDeviceAdmin,
                    )
                }

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LauncherSetupFooterButton(
                        icon = Icons.Rounded.Clear,
                        label = stringResource(
                            if (isStartupPrompt) R.string.launcher_setup_later else R.string.launcher_setup_close,
                        ),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )

                    if (onDontAskAgain != null && isStartupPrompt) {
                        LauncherSetupFooterButton(
                            icon = Icons.Rounded.Check,
                            label = stringResource(R.string.launcher_setup_dont_ask),
                            onClick = onDontAskAgain,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LauncherSetupOptionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    helperHint: String?,
    badgeText: String?,
    badgeSuccess: Boolean,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(16.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "LauncherSetupOptionScale",
    )

    val titleColor by animateColorAsState(
        targetValue = if (isFocused) colors.accentPrimary else colors.textPrimary,
        label = "LauncherSetupOptionColor",
    )

    val containerBrush = if (isFocused) {
        Brush.linearGradient(
            listOf(colors.surfaceCardFocused, colors.surfaceCardFocused.copy(alpha = 0.85f)),
        )
    } else if (isPrimary) {
        Brush.linearGradient(
            listOf(colors.accentPrimary.copy(alpha = 0.14f), colors.surfaceCard.copy(alpha = 0.85f)),
        )
    } else {
        Brush.linearGradient(
            listOf(colors.surfaceCard, colors.surfaceCard.copy(alpha = 0.65f)),
        )
    }

    val borderStroke = when {
        isFocused -> BorderStroke(1.8.dp, Brush.linearGradient(LunmedGradients.focusRimStops(colors)))
        isPrimary -> BorderStroke(1.dp, colors.accentPrimary.copy(alpha = 0.45f))
        else -> BorderStroke(1.dp, colors.borderGlass)
    }

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
            border = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
            focusedBorder = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
        ),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(containerBrush, shape)
            .border(borderStroke, shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            badgeSuccess -> colors.statusSuccess.copy(alpha = 0.20f)
                            isFocused || isPrimary -> colors.accentPrimary.copy(alpha = 0.22f)
                            else -> Color(0x14FFFFFF)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (badgeSuccess) Icons.Rounded.Check else icon,
                    contentDescription = null,
                    tint = when {
                        badgeSuccess -> colors.statusSuccess
                        isFocused || isPrimary -> colors.accentPrimary
                        else -> colors.textSecondary
                    },
                    modifier = Modifier.size(22.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = title,
                        style = LunmedTheme.typography.bodyPrimary.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = titleColor,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (badgeText != null) {
                        val badgeColor = when {
                            badgeSuccess -> colors.statusSuccess
                            isPrimary -> colors.accentPrimary
                            else -> colors.textMuted
                        }
                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.18f), CircleShape)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = badgeText,
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = badgeColor,
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textSecondary,
                )

                if (!helperHint.isNullOrBlank()) {
                    Text(
                        text = helperHint,
                        style = LunmedTheme.typography.labelMeta.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = colors.accentPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun LauncherSetupFooterButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val shape = RoundedCornerShape(12.dp)
    var isFocused by remember { mutableStateOf(false) }

    val borderStroke = if (isFocused) {
        BorderStroke(1.5.dp, Brush.linearGradient(LunmedGradients.focusRimStops(colors)))
    } else {
        BorderStroke(1.dp, colors.borderGlass)
    }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isFocused) colors.surfaceCardFocused else colors.surfaceCard.copy(alpha = 0.7f),
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textSecondary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
            focusedBorder = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
        ),
        modifier = modifier
            .clip(shape)
            .border(borderStroke, shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isFocused) colors.accentPrimary else colors.textSecondary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = LunmedTheme.typography.labelAction,
                color = if (isFocused) colors.accentPrimary else colors.textSecondary,
            )
        }
    }
}
