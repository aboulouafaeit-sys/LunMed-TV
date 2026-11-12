package com.lunmed.tv.feature.apps.components

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
fun AppLibraryContextMenuDialog(
    app: AppInfo,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleHidden: () -> Unit,
    onChangeCategory: () -> Unit,
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
                .width(400.dp)
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
                    text = "${app.packageName} • Category: ${app.categoryId ?: "other"}",
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                ActionRow(
                    label = "Open Application",
                    onClick = onLaunch,
                    modifier = Modifier.focusRequester(firstFocus),
                )
                ActionRow(
                    label = if (app.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    onClick = onToggleFavorite,
                )
                ActionRow(
                    label = if (app.isHidden) "Unhide Application" else "Hide Application",
                    onClick = onToggleHidden,
                )
                ActionRow(
                    label = "Change Category",
                    onClick = onChangeCategory,
                )
                ActionRow(
                    label = "App Info",
                    onClick = onOpenAppInfo,
                )
                ActionRow(
                    label = "Cancel",
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
fun CategoryAssignmentDialog(
    app: AppInfo,
    onSelectCategory: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    val firstFocus = remember { FocusRequester() }

    val categories = listOf(
        "media" to "Entertainment & Media",
        "games" to "Games",
        "utilities" to "Utilities & Productivity",
        "system" to "System",
        "other" to "Other",
    )

    LaunchedEffect(app.packageName) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .background(colors.surfaceElevated, LunmedTheme.shapes.panel)
                .padding(spacing.lg),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                Text(
                    text = "Assign Category",
                    style = LunmedTheme.typography.headlineSection,
                    color = colors.textPrimary,
                )
                Text(
                    text = "Select category for ${app.label}",
                    style = LunmedTheme.typography.bodySecondary,
                    color = colors.textMuted,
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                categories.forEachIndexed { index, (catId, title) ->
                    val isCurrent = (app.categoryId ?: "other") == catId
                    ActionRow(
                        label = if (isCurrent) "✓ $title (Current)" else title,
                        onClick = { onSelectCategory(catId) },
                        modifier = if (index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                    )
                }

                ActionRow(
                    label = "Cancel",
                    onClick = onDismiss,
                )
            }
        }
    }
}

@Composable
private fun ActionRow(
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
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = shape),
            focusedBorder = Border(border = BorderStroke(2.dp, colors.borderFocused), shape = shape),
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

