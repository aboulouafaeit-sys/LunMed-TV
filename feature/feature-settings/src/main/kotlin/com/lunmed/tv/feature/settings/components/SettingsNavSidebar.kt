package com.lunmed.tv.feature.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
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
    val shape = LunmedTheme.shapes.cardSmall
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            isSelected -> colors.surfaceElevated
            else -> Color.Transparent
        },
        animationSpec = anim.focusSpec(),
        label = "SidebarItemBg",
    )

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
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = contentColor,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = shape)
            } else {
                Border.None
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
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = LunmedTheme.typography.titleCard,
                    color = contentColor,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                    maxLines = 1,
                )
            }
        }
    }
}

