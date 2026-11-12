package com.lunmed.tv.feature.apps.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.graphics.SolidColor
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
import com.lunmed.tv.feature.apps.model.AppSortOrder
import com.lunmed.tv.feature.apps.model.CategoryFilterItem

@Composable
fun AppLibraryControls(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedCategory: String,
    categories: List<CategoryFilterItem>,
    onCategorySelected: (String) -> Unit,
    currentSortOrder: AppSortOrder,
    onCycleSortOrder: () -> Unit,
    availableLetters: Set<Char> = emptySet(),
    onLetterSelected: (Char) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val spacing = LunmedTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.overscanHorizontal),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // Top line: Search field + Sort order toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AppLibrarySearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChanged,
                modifier = Modifier.weight(1f, fill = false),
            )

            Spacer(modifier = Modifier.width(spacing.md))

            AppSortOrderButton(
                sortOrder = currentSortOrder,
                onClick = onCycleSortOrder,
            )
        }

        // Middle line: Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            contentPadding = PaddingValues(vertical = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .lunmedFocusGroupRestorer(),
        ) {
            items(
                items = categories,
                key = { it.id },
            ) { category ->
                val isSelected = category.id == selectedCategory
                CategoryFilterPill(
                    title = category.title,
                    count = category.count,
                    isSelected = isSelected,
                    onClick = { onCategorySelected(category.id) },
                )
            }
        }

        // Bottom line: A-Z Fast Jump Alphabet Rail (Zero-typing TV remote navigation)
        if (availableLetters.isNotEmpty()) {
            AppLibraryAlphabetRail(
                availableLetters = availableLetters,
                onLetterSelected = onLetterSelected,
            )
        }
    }
}

@Composable
private fun AppLibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    var isFocused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colors.borderFocused else colors.borderSubtle,
        animationSpec = anim.focusSpec(),
        label = "SearchBorderColor",
    )

    Box(
        modifier = modifier
            .width(360.dp)
            .height(40.dp)
            .background(colors.surfaceElevated, LunmedTheme.shapes.pill)
            .background(
                color = if (isFocused) colors.surfaceCardFocused else Color.Transparent,
                shape = LunmedTheme.shapes.pill,
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = if (isFocused) colors.accentPrimary else colors.textMuted,
                modifier = Modifier.size(18.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search applications...",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textMuted,
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = LunmedTheme.typography.bodyPrimary.copy(color = colors.textPrimary),
                    cursorBrush = SolidColor(colors.accentPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                )
            }

            if (query.isNotEmpty()) {
                Surface(
                    onClick = { onQueryChange("") },
                    shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = colors.textSecondary,
                        focusedContentColor = colors.accentPrimary,
                    ),
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear search",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterPill(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    var isFocused by remember { mutableStateOf(false) }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1.0f,
        animationSpec = anim.scaleSpringSpec(),
        label = "CatPillScale",
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

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            isSelected -> colors.surfaceElevated
            else -> Color.Transparent
        },
        animationSpec = anim.focusSpec(),
        label = "CatPillBg",
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.accentPrimary
            isSelected -> colors.textPrimary
            else -> colors.textSecondary
        },
        animationSpec = anim.focusSpec(),
        label = "CatPillContent",
    )

    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = contentColor,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(1.dp, colors.accentPrimary.copy(alpha = 0.5f)), shape = LunmedTheme.shapes.pill)
            } else {
                Border.None
            },
            focusedBorder = Border(
                border = BorderStroke(1.6.dp, goldRimBrush),
                shape = LunmedTheme.shapes.pill,
            ),
        ),
        modifier = Modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = LunmedTheme.typography.labelAction,
                color = contentColor,
            )
            if (count > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) colors.accentPrimary.copy(alpha = 0.2f) else colors.surfaceCard,
                            shape = LunmedTheme.shapes.pill,
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = count.toString(),
                        style = LunmedTheme.typography.labelMeta,
                        color = if (isSelected) colors.accentPrimary else colors.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppSortOrderButton(
    sortOrder: AppSortOrder,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    var isFocused by remember { mutableStateOf(false) }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.06f else 1.0f,
        animationSpec = anim.scaleSpringSpec(),
        label = "SortPillScale",
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
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.pill),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceElevated,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textSecondary,
            focusedContentColor = colors.accentPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = LunmedTheme.shapes.pill),
            focusedBorder = Border(
                border = BorderStroke(1.6.dp, goldRimBrush),
                shape = LunmedTheme.shapes.pill,
            ),
        ),
        modifier = Modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Sort,
                contentDescription = "Sort order",
                tint = if (isFocused) colors.accentPrimary else colors.textSecondary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = sortOrder.displayName,
                style = LunmedTheme.typography.labelAction,
                color = if (isFocused) colors.accentPrimary else colors.textPrimary,
            )
        }
    }
}

/**
 * World-class A-Z Fast Alphabet Jump Rail for 10-foot TV Remote Navigation.
 * Enables zero-typing navigation across vast TV app libraries.
 */
@Composable
private fun AppLibraryAlphabetRail(
    availableLetters: Set<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    val alphabet = remember { ('A'..'Z').toList() + '#' }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .lunmedFocusGroupRestorer(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
    ) {
        items(
            items = alphabet,
            key = { it },
        ) { char ->
            val isAvailable = availableLetters.contains(char)
            AlphabetPill(
                char = char,
                isAvailable = isAvailable,
                onClick = { if (isAvailable) onLetterSelected(char) },
            )
        }
    }
}

@Composable
private fun AlphabetPill(
    char: Char,
    isAvailable: Boolean,
    onClick: () -> Unit,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isFocused) 1.22f else 1.0f,
        label = "AlphaPillScale",
    )

    Surface(
        onClick = onClick,
        enabled = isAvailable,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(8.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isAvailable) Color(0x18FFFFFF) else Color(0x06FFFFFF),
            focusedContainerColor = colors.accentPrimary,
            contentColor = if (isAvailable) colors.textPrimary else Color(0x35FFFFFF),
            focusedContentColor = Color(0xFF0A0C14),
            disabledContainerColor = Color(0x06FFFFFF),
            disabledContentColor = Color(0x35FFFFFF),
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, if (isAvailable) Color(0x25FFFFFF) else Color.Transparent),
                shape = RoundedCornerShape(8.dp),
            ),
            focusedBorder = Border(
                border = BorderStroke(1.5.dp, Color.White),
                shape = RoundedCornerShape(8.dp),
            ),
        ),
        modifier = Modifier
            .size(28.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = char.toString(),
                style = LunmedTheme.typography.labelAction.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isAvailable) FontWeight.ExtraBold else FontWeight.Normal,
                ),
            )
        }
    }
}

