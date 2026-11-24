package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.lunmed.tv.core.design.tokens.LunmedGradients

data class DiagnosticItem(
    val label: String,
    val value: String,
    val isHighlight: Boolean = false,
)

/**
 * World-class Cybernetic Diagnostic Telemetry Card.
 * Displays hardware specifications in an authentic dual-column telemetry grid.
 */
@Composable
fun DiagnosticInfoCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    items: List<DiagnosticItem>,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "DiagCardScale",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) Color(0xF9161A28) else Color(0xF410121C),
        animationSpec = tween(durationMillis = 280),
        label = "DiagCardContainer",
    )

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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header Row: Icon + Title + Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(colors.surfaceElevated, colors.surfaceDeep),
                            ),
                        )
                        .border(
                            1.dp,
                            if (isFocused) colors.accentPrimary.copy(alpha = 0.6f) else Color(0x20FFFFFF),
                            RoundedCornerShape(12.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isFocused) colors.accentPrimary else colors.textPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Column {
                    Text(
                        text = title,
                        style = LunmedTheme.typography.titleCard.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        ),
                        color = colors.textPrimary,
                    )
                    Text(
                        text = subtitle,
                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.5.sp),
                        color = colors.textMuted,
                    )
                }
            }

            // Specs Table / Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x10FFFFFF))
                    .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        rowItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x0EFFFFFF))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = item.label,
                                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                        color = colors.textMuted,
                                    )
                                    Text(
                                        text = item.value,
                                        style = LunmedTheme.typography.bodySecondary.copy(
                                            fontWeight = if (item.isHighlight) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                        ),
                                        color = if (item.isHighlight) colors.accentPrimary else colors.textPrimary,
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
