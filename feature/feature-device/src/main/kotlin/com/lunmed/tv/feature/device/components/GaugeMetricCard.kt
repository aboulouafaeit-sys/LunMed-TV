package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
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

@Composable
fun GaugeMetricCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    usedFormatted: String,
    totalFormatted: String,
    availableFormatted: String,
    usageRatio: Float,
    statusText: String,
    isWarning: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val spacing = LunmedTheme.spacing
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) colors.surfaceCardFocused else colors.surfaceCard,
        animationSpec = anim.focusSpec(),
        label = "GaugeCardContainer",
    )

    val gaugeColor = when {
        isWarning -> colors.statusError
        usageRatio > 0.85f -> colors.statusError
        usageRatio > 0.70f -> colors.statusWarning
        else -> colors.accentPrimary
    }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.cardMedium),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = LunmedTheme.shapes.cardMedium),
            focusedBorder = Border(border = BorderStroke(2.dp, colors.borderFocused), shape = LunmedTheme.shapes.cardMedium),
        ),
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Header Row: Icon + Title + Status Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(colors.surfaceElevated, LunmedTheme.shapes.pill),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isFocused) colors.accentPrimary else colors.textPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = LunmedTheme.typography.titleCard,
                            color = colors.textPrimary,
                        )
                        Text(
                            text = subtitle,
                            style = LunmedTheme.typography.labelMeta,
                            color = colors.textMuted,
                        )
                    }
                }

                // Percentage Badge
                Box(
                    modifier = Modifier
                        .background(gaugeColor.copy(alpha = 0.18f), LunmedTheme.shapes.pill)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = "${(usageRatio * 100).toInt()}%",
                        style = LunmedTheme.typography.labelAction.copy(fontWeight = FontWeight.Bold),
                        color = gaugeColor,
                    )
                }
            }

            // Luxury Gradient Gauge Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(colors.surfaceElevated, LunmedTheme.shapes.pill),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = usageRatio.coerceIn(0.02f, 1f))
                        .height(8.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(gaugeColor.copy(alpha = 0.7f), gaugeColor),
                            ),
                            shape = LunmedTheme.shapes.pill,
                        ),
                )
            }

            // Metrics Readout Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "USED / TOTAL",
                        style = LunmedTheme.typography.labelMeta.copy(letterSpacing = 1.2.sp),
                        color = colors.textMuted,
                    )
                    Text(
                        text = "$usedFormatted / $totalFormatted",
                        style = LunmedTheme.typography.bodySecondary.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "AVAILABLE",
                        style = LunmedTheme.typography.labelMeta.copy(letterSpacing = 1.2.sp),
                        color = colors.textMuted,
                    )
                    Text(
                        text = availableFormatted,
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textSecondary,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "STATUS",
                        style = LunmedTheme.typography.labelMeta.copy(letterSpacing = 1.2.sp),
                        color = colors.textMuted,
                    )
                    Text(
                        text = statusText,
                        style = LunmedTheme.typography.bodySecondary.copy(fontWeight = FontWeight.SemiBold),
                        color = gaugeColor,
                    )
                }
            }
        }
    }
}

