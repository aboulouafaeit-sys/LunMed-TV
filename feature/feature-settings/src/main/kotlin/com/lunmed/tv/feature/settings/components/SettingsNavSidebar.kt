package com.lunmed.tv.feature.settings.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.feature.settings.model.SettingsSection

@Composable
fun SettingsNavSidebar(
    selectedSection: SettingsSection,
    onSelectSection: (SettingsSection) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LunmedTheme.spacing

    Column(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .lunmedFocusGroupRestorer(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsSection.entries.forEach { section ->
            val isSelected = section == selectedSection
            val icon = when (section) {
                SettingsSection.APPEARANCE -> Icons.Rounded.Palette
                SettingsSection.ANIMATIONS -> Icons.Rounded.Animation
                SettingsSection.HOME_SCREEN -> Icons.Rounded.Home
                SettingsSection.DEFAULT_LAUNCHER -> Icons.Rounded.Tune
                SettingsSection.LANGUAGE -> Icons.Rounded.Language
                SettingsSection.ABOUT -> Icons.Rounded.Info
            }

            SidebarItem(
                title = section.title,
                subtitle = section.subtitle,
                icon = icon,
                isSelected = isSelected,
                onClick = { onSelectSection(section) },
            )
        }
    }
}

@Composable
private fun SidebarItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val shape = RoundedCornerShape(16.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1.0f,
        animationSpec = anim.scaleSpringSpec(),
        label = "SidebarItemScale",
    )

    val containerBrush = when {
        isFocused -> Brush.horizontalGradient(
            listOf(
                colors.surfaceCardFocused,
                colors.surfaceCardFocused.copy(alpha = 0.85f),
            ),
        )
        isSelected -> Brush.horizontalGradient(
            listOf(
                colors.surfaceElevated.copy(alpha = 0.95f),
                colors.surfaceElevated.copy(alpha = 0.60f),
            ),
        )
        else -> Brush.horizontalGradient(
            listOf(Color.Transparent, Color.Transparent),
        )
    }

    val contentColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.accentPrimary
            isSelected -> colors.textPrimary
            else -> colors.textSecondary
        },
        animationSpec = anim.focusSpec(),
        label = "SidebarItemContent",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            contentColor = contentColor,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(
                    border = BorderStroke(1.dp, Color(0x33FFFFFF)),
                    shape = shape,
                )
            } else {
                Border.None
            },
            focusedBorder = Border(
                border = BorderStroke(
                    1.8.dp,
                    Brush.linearGradient(
                        listOf(Color(0xFFFFF8E7), colors.accentPrimary, Color(0xFFC49A45)),
                    ),
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
            .background(containerBrush, shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Active Gold Indicator Pill
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(width = 3.5.dp, height = 24.dp)
                        .background(colors.accentPrimary, RoundedCornerShape(2.dp)),
                )
            }

            // Tactile Icon Circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFocused) colors.accentPrimary.copy(alpha = 0.22f)
                        else if (isSelected) colors.accentPrimary.copy(alpha = 0.14f)
                        else Color(0x14FFFFFF),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused || isSelected) colors.accentPrimary else colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary.copy(
                        fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                    color = contentColor,
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
