package com.lunmed.tv.feature.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo

@Composable
fun AppContextMenuDialog(
    app: AppInfo,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onHide: () -> Unit,
    onOpenAppInfo: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(app.packageName) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(380.dp)
                .background(colors.surfaceElevated, LunmedTheme.shapes.panel)
                .padding(spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = app.label,
                    style = LunmedTheme.typography.headlineSection,
                    color = colors.textPrimary,
                )
                Text(
                    text = app.packageName,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                ContextActionItem(
                    label = "Open Application",
                    onClick = onLaunch,
                    modifier = Modifier.focusRequester(firstFocus),
                )
                ContextActionItem(
                    label = if (app.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    onClick = onToggleFavorite,
                )
                ContextActionItem(
                    label = "Hide from Home",
                    onClick = onHide,
                )
                ContextActionItem(
                    label = "App Info",
                    onClick = onOpenAppInfo,
                )
                ContextActionItem(
                    label = "Cancel",
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun ContextActionItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val shape = LunmedTheme.shapes.cardSmall

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceCard,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(BorderStroke(1.dp, colors.borderSubtle), shape),
            focusedBorder = Border(BorderStroke(2.dp, colors.borderFocused), shape),
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = LunmedTheme.typography.bodyPrimary,
            )
        }
    }
}

