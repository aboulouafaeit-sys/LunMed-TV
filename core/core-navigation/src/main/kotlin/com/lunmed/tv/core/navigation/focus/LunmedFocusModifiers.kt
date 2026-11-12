package com.lunmed.tv.core.navigation.focus

import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRestorer

/**
 * Applies Compose TV focus group + automatic child focus restoration
 * so horizontal rails remember their last focused item when navigating vertically.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.lunmedFocusGroupRestorer(
    fallback: FocusRequester = FocusRequester.Default,
): Modifier = this
    .focusGroup()
    .focusRestorer(fallback)

@Composable
fun rememberFocusMemoryRegistry(): FocusMemoryRegistry = remember {
    FocusMemoryRegistry()
}

