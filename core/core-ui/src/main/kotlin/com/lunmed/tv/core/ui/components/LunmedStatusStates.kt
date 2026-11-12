package com.lunmed.tv.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme

@Composable
fun LunmedEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val typography = LunmedTheme.typography
    val spacing = LunmedTheme.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = typography.titleLarge,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = subtitle,
            style = typography.bodySecondary,
            color = colors.textSecondary,
        )
    }
}

