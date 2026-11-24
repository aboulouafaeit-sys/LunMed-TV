package com.lunmed.tv.feature.device.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeveloperBoard
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
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
import com.lunmed.tv.core.model.HardwarePlatformDiagnostics

/**
 * World-class Cybernetic Silicon SoC Processor Chip Badge.
 * Replaces primitive platform text with an authentic etched microprocessor die.
 */
@Composable
fun SiliconSoCChipBadge(
    platform: HardwarePlatformDiagnostics,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "SiliconChipScale",
    )

    val focusRimBrush = remember(colors.accentPrimary, colors.accentSecondary) { LunmedGradients.focusRimBrush(colors) }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(24.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color(0xF410121C),
            focusedContainerColor = Color(0xF9141828),
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.2.dp, Color(0x30FFFFFF)),
                shape = RoundedCornerShape(24.dp),
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, focusRimBrush),
                shape = RoundedCornerShape(24.dp),
            ),
        ),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Top line: Status Pill & Silicon Core Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(colors.accentPrimary.copy(alpha = 0.35f), Color(0x22FFFFFF)),
                                    ),
                                )
                                .border(1.2.dp, colors.accentPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeveloperBoard,
                                contentDescription = null,
                                tint = colors.accentPrimary,
                                modifier = Modifier.size(24.dp),
                            )
                        }

                        Column {
                            Text(
                                text = "HARDWARE SILICON PLATFORM",
                                style = LunmedTheme.typography.labelAction.copy(
                                    letterSpacing = 1.8.sp,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                ),
                                color = colors.accentPrimary,
                            )
                            Text(
                                text = "${platform.manufacturer} ${platform.model}",
                                style = LunmedTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                ),
                                color = colors.textPrimary,
                            )
                        }
                    }

                    // Active SoC Engine Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1800FF87))
                            .border(1.dp, Color(0x4000FF87), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(Color(0xFF00FF87), CircleShape),
                            )
                            Text(
                                text = "SILICON ONLINE",
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                ),
                                color = Color(0xFF00FF87),
                            )
                        }
                    }
                }

                // Center: Microprocessor Die Representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0A0C14),
                                    Color(0xFF141824),
                                    Color(0xFF0A0C14),
                                ),
                            ),
                        )
                        .border(1.dp, Color(0x25FFFFFF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "CHIPSET DIE ARCHITECTURE",
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                ),
                                color = colors.textMuted,
                            )
                            Text(
                                text = platform.chipArchitecture,
                                style = LunmedTheme.typography.titleCard.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                ),
                                color = colors.accentPrimary,
                            )
                            Text(
                                text = "Board Target: ${platform.socOrBoard} • Product: ${platform.product}",
                                style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                color = colors.textSecondary,
                            )
                        }

                        // Gold Circuit Core Emblem
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0x35FFB300), Color(0x10FFB300)),
                                    ),
                                )
                                .border(1.5.dp, colors.accentPrimary, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "${platform.cpuCores}",
                                    style = LunmedTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                    ),
                                    color = colors.accentPrimary,
                                )
                                Text(
                                    text = "CORES",
                                    style = LunmedTheme.typography.labelMeta.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                    ),
                                    color = colors.textMuted,
                                )
                            }
                        }
                    }
                }

                // Bottom 4-Column Micro Spec Matrix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PlatformMicroSpec(
                        label = "CPU ARCH",
                        value = platform.supportedAbis.take(12),
                        modifier = Modifier.weight(1f),
                    )
                    PlatformMicroSpec(
                        label = "ANDROID OS",
                        value = "v${platform.androidRelease} (API ${platform.sdkInt})",
                        modifier = Modifier.weight(1f),
                    )
                    PlatformMicroSpec(
                        label = "SECURITY",
                        value = platform.securityPatch,
                        modifier = Modifier.weight(1f),
                    )
                    PlatformMicroSpec(
                        label = "FIRMWARE",
                        value = platform.buildDisplay.take(14),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformMicroSpec(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x12FFFFFF))
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                ),
                color = colors.textMuted,
            )
            Text(
                text = value,
                style = LunmedTheme.typography.bodySecondary.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = colors.textPrimary,
                maxLines = 1,
            )
        }
    }
}

