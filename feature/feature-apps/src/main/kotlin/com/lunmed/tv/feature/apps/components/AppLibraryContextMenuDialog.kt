package com.lunmed.tv.feature.apps.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.ui.artwork.AppArtwork
import com.lunmed.tv.core.ui.artwork.AppArtworkCache
import com.lunmed.tv.core.ui.components.AdaptiveAuraEngine

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
    val context = LocalContext.current
    val colors = LunmedTheme.colors
    val firstFocus = remember { FocusRequester() }

    val artwork by produceState<AppArtwork?>(initialValue = null, key1 = app.packageName) {
        value = AppArtworkCache.load(context, app.packageName, app.activityName)
    }

    val appAura = remember(app.packageName, app.categoryId) {
        AdaptiveAuraEngine.auraForApp(app, colors.accentPrimary)
    }

    LaunchedEffect(app.packageName) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(440.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.surfaceElevated.copy(alpha = 0.96f),
                            colors.surfaceCard.copy(alpha = 0.98f),
                            Color(0xFF08080C),
                        ),
                    ),
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.60f),
                                colors.borderSubtle,
                                Color(0x18FFFFFF),
                            ),
                        ),
                    ),
                    RoundedCornerShape(24.dp),
                )
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Header with artwork and aura
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(appAura.copy(alpha = 0.45f), colors.surfaceCard),
                                ),
                            )
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        val art = artwork
                        if (art != null) {
                            Image(
                                bitmap = art.bitmap,
                                contentDescription = app.label,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                            )
                        } else {
                            Text(
                                text = app.label.take(1).uppercase(),
                                style = LunmedTheme.typography.headlineSection,
                                color = colors.accentPrimary,
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.label,
                            style = LunmedTheme.typography.headlineSection.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                            ),
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "${app.packageName} • ${app.categoryId ?: "other"}",
                            style = LunmedTheme.typography.labelMeta,
                            color = colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (app.isFavorite) {
                                Box(
                                    modifier = Modifier
                                        .background(colors.accentPrimary.copy(alpha = 0.20f), CircleShape)
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "★ Favorite",
                                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                                        color = colors.accentPrimary,
                                    )
                                }
                            }
                            if (app.isHidden) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0x22FF5252), CircleShape)
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "Hidden",
                                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                                        color = Color(0xFFFF5252),
                                    )
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x33FFFFFF), Color.Transparent),
                            ),
                        ),
                )

                // Action Items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LibraryActionPill(
                        icon = Icons.Rounded.PlayArrow,
                        title = "Open Application",
                        subtitle = "تشغيل التطبيق فوراً",
                        isPrimary = true,
                        onClick = onLaunch,
                        modifier = Modifier.focusRequester(firstFocus),
                    )

                    LibraryActionPill(
                        icon = Icons.Rounded.Star,
                        title = if (app.isFavorite) "Remove from Favorites" else "Add to Favorites",
                        subtitle = if (app.isFavorite) "إزالة من قائمة المفضلة" else "تثبيت في المفضلة",
                        isPrimary = false,
                        onClick = onToggleFavorite,
                    )

                    LibraryActionPill(
                        icon = if (app.isHidden) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        title = if (app.isHidden) "Unhide Application" else "Hide Application",
                        subtitle = if (app.isHidden) "إظهار التطبيق في المكتبة والواجهة" else "إخفاء التطبيق من الشاشة",
                        isPrimary = false,
                        onClick = onToggleHidden,
                    )

                    LibraryActionPill(
                        icon = Icons.Rounded.Tune,
                        title = "Change Category",
                        subtitle = "تعديل تصنيف التطبيق (وسائط، ألعاب، أدوات)",
                        isPrimary = false,
                        onClick = onChangeCategory,
                    )

                    LibraryActionPill(
                        icon = Icons.Rounded.Info,
                        title = "App Info & Diagnostics",
                        subtitle = "تفاصيل الحزمة ومعلومات النظام",
                        isPrimary = false,
                        onClick = onOpenAppInfo,
                    )

                    LibraryActionPill(
                        icon = Icons.Rounded.Clear,
                        title = "Dismiss",
                        subtitle = "إغلاق القائمة والرجوع",
                        isPrimary = false,
                        onClick = onDismiss,
                    )
                }
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
    val firstFocus = remember { FocusRequester() }

    val categories = listOf(
        "media" to "Entertainment & Media (وسائط وترفيه)",
        "games" to "Games & Arcade (ألعاب وتسلية)",
        "utilities" to "Utilities & Tools (أدوات وإنتاجية)",
        "system" to "System & Settings (أدوات النظام)",
        "other" to "General / Other (تطبيقات عامة)",
    )

    LaunchedEffect(app.packageName) {
        firstFocus.requestFocus()
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(440.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.surfaceElevated.copy(alpha = 0.96f),
                            colors.surfaceCard.copy(alpha = 0.98f),
                            Color(0xFF08080C),
                        ),
                    ),
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                colors.accentPrimary.copy(alpha = 0.60f),
                                colors.borderSubtle,
                                Color(0x18FFFFFF),
                            ),
                        ),
                    ),
                    RoundedCornerShape(24.dp),
                )
                .padding(24.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Assign Category (تصنيف التطبيق)",
                        style = LunmedTheme.typography.headlineSection.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select primary category for ${app.label}",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textMuted,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x33FFFFFF), Color.Transparent),
                            ),
                        ),
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEachIndexed { index, (catId, title) ->
                        val isCurrent = (app.categoryId ?: "other") == catId
                        LibraryActionPill(
                            icon = if (isCurrent) Icons.Rounded.Check else Icons.Rounded.Category,
                            title = title,
                            subtitle = if (isCurrent) "Current Selection (التصنيف الحالي)" else "Tap to assign",
                            isPrimary = isCurrent,
                            onClick = { onSelectCategory(catId) },
                            modifier = if (index == 0) Modifier.focusRequester(firstFocus) else Modifier,
                        )
                    }

                    LibraryActionPill(
                        icon = Icons.Rounded.Clear,
                        title = "Cancel",
                        subtitle = "إلغاء التغيير",
                        isPrimary = false,
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryActionPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val animTokens = LunmedTheme.animation
    val shape = RoundedCornerShape(14.dp)
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "LibraryActionScale",
    )

    val contentColor by animateColorAsState(
        targetValue = if (isFocused) colors.accentPrimary else colors.textPrimary,
        label = "LibraryActionColor",
    )

    val containerBrush = if (isFocused) {
        Brush.linearGradient(
            colors = listOf(
                colors.surfaceCardFocused,
                colors.surfaceCardFocused.copy(alpha = 0.85f),
            ),
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                colors.surfaceCard,
                colors.surfaceCard.copy(alpha = 0.60f),
            ),
        )
    }

    val borderStroke = if (isFocused) {
        BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(
                    Color(0xFFFFF8E7),
                    colors.accentPrimary,
                    Color(0xFFC49A45),
                ),
            ),
        )
    } else {
        BorderStroke(1.dp, Color(0x18FFFFFF))
    }

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
            focusedBorder = Border(border = BorderStroke(0.dp, Color.Transparent), shape = shape),
        ),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(containerBrush, shape)
            .border(borderStroke, shape)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFocused) colors.accentPrimary.copy(alpha = 0.20f)
                        else if (isPrimary) colors.accentPrimary.copy(alpha = 0.12f)
                        else Color(0x12FFFFFF),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFocused) colors.accentPrimary else if (isPrimary) colors.accentPrimary else colors.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = LunmedTheme.typography.bodyPrimary.copy(
                        fontWeight = if (isFocused || isPrimary) FontWeight.Bold else FontWeight.Medium,
                    ),
                    color = contentColor,
                )
                Text(
                    text = subtitle,
                    style = LunmedTheme.typography.labelMeta,
                    color = colors.textMuted,
                )
            }
        }
    }
}
