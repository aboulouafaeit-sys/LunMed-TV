package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme

data class DiagnosticItem(
    val label: String,
    val value: String,
    val isHighlight: Boolean = false,
)

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
    val anim = LunmedTheme.animation
    val spacing = LunmedTheme.spacing
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = anim.focusSpec(),
        label = "DiagCardScale",
    )

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) colors.surfaceCardFocused else colors.surfaceCard,
        animationSpec = anim.focusSpec(),
        label = "DiagCardContainer",
    )

    val goldRimBrush = remember(colors.accentPrimary) {
        Brush.linearGradient(
            listOf(
                Color(0xFFFFF8E7),
                colors.accentPrimary,
                colors.accentSecondary,
            ),
        )
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
            focusedBorder = Border(border = BorderStroke(2.dp, goldRimBrush), shape = LunmedTheme.shapes.cardMedium),
        ),
        modifier = modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(colors.surfaceElevated, colors.surfaceDeep),
                            ),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = if (isFocused) colors.accentPrimary.copy(alpha = 0.5f) else Color(0x18FFFFFF),
                            shape = RoundedCornerShape(10.dp),
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
                        style = LunmedTheme.typography.titleCard.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Text(
                        text = subtitle,
                        style = LunmedTheme.typography.labelMeta,
                        color = colors.textMuted,
                    )
                }
            }

            // Specs Table / Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceElevated.copy(alpha = 0.55f), LunmedTheme.shapes.cardMedium)
                    .border(1.dp, Color(0x12FFFFFF), LunmedTheme.shapes.cardMedium)
                    .padding(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item.label,
                            style = LunmedTheme.typography.bodySecondary,
                            color = colors.textMuted,
                        )
                        Text(
                            text = item.value,
                            style = LunmedTheme.typography.bodySecondary.copy(
                                fontWeight = if (item.isHighlight) FontWeight.Bold else FontWeight.Normal,
                            ),
                            color = if (item.isHighlight) colors.accentPrimary else colors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}
