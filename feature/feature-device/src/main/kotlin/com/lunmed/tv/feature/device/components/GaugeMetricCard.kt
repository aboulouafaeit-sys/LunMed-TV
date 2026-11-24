package com.lunmed.tv.feature.device.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.lunmed.tv.core.design.tokens.LunmedGradients
import kotlin.math.cos
import kotlin.math.sin

/**
 * World-class Cybernetic Radial Arc HUD Metric Gauge.
 * Replaces primitive horizontal progress bars with an authentic 260-degree circular arc gauge,
 * real-time tip glow beacon, and deep luxury telemetry telemetry readout.
 */
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
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "GaugeCardScale",
    )

    val animatedRatio by animateFloatAsState(
        targetValue = usageRatio.coerceIn(0.01f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "GaugeRatioAnim",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xF9161A28) else Color(0xF410121C),
        animationSpec = tween(durationMillis = 280),
        label = "GaugeCardContainer",
    )

    val gaugeColor = when {
        isWarning -> Color(0xFFFF3D00)
        usageRatio > 0.85f -> Color(0xFFFF5252)
        usageRatio > 0.70f -> Color(0xFFFFB300)
        else -> Color(0xFF00FF87)
    }

    val focusRimBrush = remember(colors.accentPrimary, colors.accentSecondary) { LunmedGradients.focusRimBrush(colors) }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(24.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = Color(0xF9161A28),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 1. Cybernetic Radial Arc Gauge
                Box(
                    modifier = Modifier.size(118.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 11.dp.toPx()
                        val arcPadding = strokeWidth / 2f
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val startAngle = 140f
                        val totalSweep = 260f
                        val activeSweep = totalSweep * animatedRatio

                        // Background Track Arc
                        drawArc(
                            color = Color(0x18FFFFFF),
                            startAngle = startAngle,
                            sweepAngle = totalSweep,
                            useCenter = false,
                            topLeft = Offset(arcPadding, arcPadding),
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        )

                        // Active Glowing Arc
                        val arcBrush = Brush.sweepGradient(
                            listOf(
                                Color(0xFF00FF87),
                                Color(0xFFFFB300),
                                gaugeColor,
                            ),
                        )

                        drawArc(
                            brush = arcBrush,
                            startAngle = startAngle,
                            sweepAngle = activeSweep,
                            useCenter = false,
                            topLeft = Offset(arcPadding, arcPadding),
                            size = arcSize,
                            style = Stroke(width = strokeWidth + 1.dp.toPx(), cap = StrokeCap.Round),
                        )

                        // Tip Glow Beacon
                        val currentAngleRad = Math.toRadians((startAngle + activeSweep).toDouble())
                        val radius = (size.width - strokeWidth) / 2f
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val tipX = centerX + radius * cos(currentAngleRad).toFloat()
                        val tipY = centerY + radius * sin(currentAngleRad).toFloat()

                        drawCircle(
                            color = gaugeColor.copy(alpha = 0.5f),
                            radius = 7.dp.toPx(),
                            center = Offset(tipX, tipY),
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.5.dp.toPx(),
                            center = Offset(tipX, tipY),
                        )
                    }

                    // Inside Arc Readout: Big percentage + Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "${(usageRatio * 100).toInt()}%",
                            style = LunmedTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                letterSpacing = (-0.5).sp,
                            ),
                            color = colors.textPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(gaugeColor.copy(alpha = 0.20f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = statusText,
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                ),
                                color = gaugeColor,
                            )
                        }
                    }
                }

                // 2. Right Telemetry Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Header Row: Icon + Title + Action Hint
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(colors.surfaceElevated, colors.surfaceDeep),
                                        ),
                                    )
                                    .border(
                                        1.dp,
                                        if (isFocused) colors.accentPrimary.copy(alpha = 0.6f) else Color(0x18FFFFFF),
                                        RoundedCornerShape(10.dp),
                                    ),
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
                                    style = LunmedTheme.typography.titleCard.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    ),
                                    color = colors.textPrimary,
                                )
                                Text(
                                    text = subtitle,
                                    style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                    color = colors.textMuted,
                                )
                            }
                        }

                        // Focus hint chip
                        if (isFocused) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colors.accentPrimary.copy(alpha = 0.15f))
                                    .border(1.dp, colors.accentPrimary.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    Text(
                                        text = "CLICK TO OPTIMIZE",
                                        style = LunmedTheme.typography.labelMeta.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                        ),
                                        color = colors.accentPrimary,
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                        contentDescription = null,
                                        tint = colors.accentPrimary,
                                        modifier = Modifier.size(12.dp),
                                    )
                                }
                            }
                        }
                    }

                    // Dual Metrics Spec Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MetricPillBox(
                            label = "USED SPACE",
                            value = usedFormatted,
                            valueColor = colors.textPrimary,
                            modifier = Modifier.weight(1f),
                        )
                        MetricPillBox(
                            label = "AVAILABLE",
                            value = availableFormatted,
                            valueColor = Color(0xFF00FF87),
                            modifier = Modifier.weight(1f),
                        )
                        MetricPillBox(
                            label = "TOTAL CAPACITY",
                            value = totalFormatted,
                            valueColor = colors.textSecondary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricPillBox(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x12FFFFFF))
            .padding(horizontal = 10.dp, vertical = 7.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp,
                ),
                color = colors.textMuted,
            )
            Text(
                text = value,
                style = LunmedTheme.typography.bodySecondary.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = valueColor,
            )
        }
    }
}
