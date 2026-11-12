package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) colors.surfaceCardFocused else colors.surfaceCard,
        animationSpec = anim.focusSpec(),
        label = "DiagCardContainer",
    )

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
            // Header
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

            // Specs Table / Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surfaceElevated.copy(alpha = 0.5f), LunmedTheme.shapes.cardMedium)
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

