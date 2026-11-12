package com.lunmed.tv.feature.apps

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.core.navigation.focus.rememberFocusMemoryRegistry
import com.lunmed.tv.core.navigation.remote.RemoteCommand
import com.lunmed.tv.core.navigation.remote.onRemoteKeyCommand
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedEmptyState
import com.lunmed.tv.core.ui.components.LunmedTopBar
import com.lunmed.tv.feature.apps.components.AppLibraryContextMenuDialog
import com.lunmed.tv.feature.apps.components.AppLibraryControls
import com.lunmed.tv.feature.apps.components.AppLibraryGridCard
import com.lunmed.tv.feature.apps.components.CategoryAssignmentDialog
import com.lunmed.tv.feature.apps.model.AppLibraryFilterEngine
import com.lunmed.tv.feature.apps.model.AppSortOrder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppLibraryRoute(
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAppInfo: (String) -> Unit,
    viewModel: AppLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage != null) {
            delay(3_500L)
            viewModel.onClearStatusMessage()
        }
    }

    AppLibraryScreen(
        uiState = uiState,
        onNavigate = onNavigate,
        onRequestDefaultLauncher = onRequestDefaultLauncher,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onCycleSortOrder = {
            val next = when (uiState.sortOrder) {
                AppSortOrder.NAME_ASC -> AppSortOrder.NAME_DESC
                AppSortOrder.NAME_DESC -> AppSortOrder.MOST_USED
                AppSortOrder.MOST_USED -> AppSortOrder.RECENTLY_USED
                AppSortOrder.RECENTLY_USED -> AppSortOrder.CUSTOM
                AppSortOrder.CUSTOM -> AppSortOrder.NAME_ASC
            }
            viewModel.onSortOrderChanged(next)
        },
        onAppFocused = viewModel::onAppFocused,
        onAppClicked = viewModel::onAppClicked,
        onAppLongClicked = viewModel::onAppLongClicked,
        onToggleFavorite = viewModel::onToggleFavorite,
        onToggleHidden = viewModel::onToggleHidden,
        onOpenCategoryPicker = viewModel::onOpenCategoryPicker,
        onDismissCategoryPicker = viewModel::onDismissCategoryPicker,
        onAssignCategory = viewModel::onAssignCategory,
        onOpenAppInfo = { app ->
            viewModel.onDismissContextMenu()
            onOpenAppInfo(app.packageName)
        },
        onDismissContextMenu = viewModel::onDismissContextMenu,
    )
}

@Composable
fun AppLibraryScreen(
    uiState: AppLibraryUiState,
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onCycleSortOrder: () -> Unit,
    onAppFocused: (AppInfo) -> Unit,
    onAppClicked: (AppInfo) -> Unit,
    onAppLongClicked: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
    onToggleHidden: (AppInfo) -> Unit,
    onOpenCategoryPicker: (AppInfo) -> Unit,
    onDismissCategoryPicker: () -> Unit,
    onAssignCategory: (AppInfo, String?) -> Unit,
    onOpenAppInfo: (AppInfo) -> Unit,
    onDismissContextMenu: () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val focusMemory = rememberFocusMemoryRegistry()
    val firstCardFocusRequester = remember { FocusRequester() }
    val itemFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }
    var hasRequestedInitialFocus by remember { mutableStateOf(false) }

    fun requesterFor(packageName: String): FocusRequester =
        itemFocusRequesters.getOrPut(packageName) { FocusRequester() }

    LaunchedEffect(uiState.visibleAppsCount) {
        if (!hasRequestedInitialFocus && uiState.visibleAppsCount > 0) {
            hasRequestedInitialFocus = true
            runCatching { firstCardFocusRequester.requestFocus() }
        }
    }

    fun dismissMenuAndRestoreFocus() {
        val anchor = focusMemory.popOverlayAnchor()
        onDismissContextMenu()
        if (anchor != null) {
            coroutineScope.launch {
                runCatching {
                    itemFocusRequesters[anchor.itemId]?.requestFocus()
                }
            }
        }
    }

    // Back handling: close dialog -> clear search -> reset category -> go Home
    BackHandler(enabled = true) {
        when {
            uiState.contextMenuApp != null -> dismissMenuAndRestoreFocus()
            uiState.categoryPickerApp != null -> onDismissCategoryPicker()
            uiState.searchQuery.isNotEmpty() -> onSearchQueryChanged("")
            uiState.selectedCategory != AppLibraryFilterEngine.CATEGORY_ALL ->
                onCategorySelected(AppLibraryFilterEngine.CATEGORY_ALL)
            else -> onNavigate(LunmedDestination.HOME)
        }
    }

    LunmedAmbientCanvas(
        modifier = Modifier.onRemoteKeyCommand { command ->
            when (command) {
                RemoteCommand.MENU -> {
                    val target = uiState.focusedApp
                    if (target != null && uiState.contextMenuApp == null && uiState.categoryPickerApp == null) {
                        focusMemory.pushOverlayAnchor()
                        onAppLongClicked(target)
                        true
                    } else {
                        false
                    }
                }
                RemoteCommand.SETTINGS -> {
                    onNavigate(LunmedDestination.SETTINGS)
                    true
                }
                else -> false
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LunmedTopBar(
                activeDestination = LunmedDestination.APPS,
                networkStatus = uiState.networkStatus,
                capabilities = uiState.capabilities,
                showClock = true,
                showDeviceIndicators = true,
                isDefaultLauncher = true,
                canRequestDefaultLauncher = false,
                onNavigate = onNavigate,
                onRequestDefaultLauncher = onRequestDefaultLauncher,
            )

            AppLibraryControls(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                selectedCategory = uiState.selectedCategory,
                categories = uiState.availableCategories,
                onCategorySelected = onCategorySelected,
                currentSortOrder = uiState.sortOrder,
                onCycleSortOrder = onCycleSortOrder,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            if (!uiState.isLoading && uiState.apps.isEmpty()) {
                LunmedEmptyState(
                    title = "No applications found",
                    subtitle = if (uiState.searchQuery.isNotBlank()) {
                        "No apps match \"${uiState.searchQuery}\""
                    } else {
                        "No applications available in this category."
                    },
                    modifier = Modifier.weight(1f),
                )
            } else {
                val columnsCount = if (uiState.capabilities?.is4kDisplay == true) 6 else 5

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnsCount),
                    state = gridState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = spacing.overscanHorizontal)
                        .lunmedFocusGroupRestorer(),
                    contentPadding = PaddingValues(
                        top = spacing.sm,
                        bottom = spacing.xl,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(spacing.cardGap),
                    verticalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    items(
                        items = uiState.apps,
                        key = { it.packageName },
                    ) { app ->
                        val isFirst = uiState.apps.firstOrNull()?.packageName == app.packageName
                        val requester = requesterFor(app.packageName)
                        val cardModifier = if (isFirst) {
                            Modifier
                                .focusRequester(firstCardFocusRequester)
                                .focusRequester(requester)
                        } else {
                            Modifier.focusRequester(requester)
                        }

                        AppLibraryGridCard(
                            app = app,
                            cardSize = uiState.cardSize,
                            sortOrder = uiState.sortOrder,
                            onFocused = {
                                focusMemory.recordFocus("app_grid", app.packageName)
                                onAppFocused(app)
                            },
                            onClick = { onAppClicked(app) },
                            onLongClick = {
                                focusMemory.recordFocus("app_grid", app.packageName)
                                focusMemory.pushOverlayAnchor()
                                onAppLongClicked(app)
                            },
                            modifier = cardModifier,
                        )
                    }
                }
            }
        }

        // Error / Status Toast
        AnimatedVisibility(
            visible = uiState.statusMessage != null,
            enter = fadeIn(LunmedTheme.animation.focusSpec()),
            exit = fadeOut(LunmedTheme.animation.focusSpec()),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = spacing.lg),
        ) {
            Box(
                modifier = Modifier
                    .background(colors.surfaceElevated, LunmedTheme.shapes.pill)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = uiState.statusMessage.orEmpty(),
                    style = LunmedTheme.typography.bodySecondary,
                    color = colors.statusWarning,
                )
            }
        }

        // Context Menu Dialog
        uiState.contextMenuApp?.let { app ->
            AppLibraryContextMenuDialog(
                app = app,
                onLaunch = {
                    dismissMenuAndRestoreFocus()
                    onAppClicked(app)
                },
                onToggleFavorite = {
                    onToggleFavorite(app)
                    dismissMenuAndRestoreFocus()
                },
                onToggleHidden = {
                    onToggleHidden(app)
                    dismissMenuAndRestoreFocus()
                },
                onChangeCategory = { onOpenCategoryPicker(app) },
                onOpenAppInfo = { onOpenAppInfo(app) },
                onDismiss = { dismissMenuAndRestoreFocus() },
            )
        }

        // Category Picker Dialog
        uiState.categoryPickerApp?.let { app ->
            CategoryAssignmentDialog(
                app = app,
                onSelectCategory = { newCategory ->
                    onAssignCategory(app, newCategory)
                },
                onDismiss = onDismissCategoryPicker,
            )
        }
    }
}

