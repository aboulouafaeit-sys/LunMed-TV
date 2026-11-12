package com.lunmed.tv.feature.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.navigation.focus.lunmedFocusGroupRestorer
import com.lunmed.tv.core.navigation.focus.rememberFocusMemoryRegistry
import com.lunmed.tv.core.navigation.remote.RemoteCommand
import com.lunmed.tv.core.navigation.remote.onRemoteKeyCommand
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedEmptyState
import com.lunmed.tv.feature.home.components.AppContextMenuDialog
import com.lunmed.tv.feature.home.components.HomeAppCard
import com.lunmed.tv.feature.home.components.HomeNavDestination
import com.lunmed.tv.feature.home.components.HomeSpotlightStage
import com.lunmed.tv.feature.home.components.HomeTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    homePressTick: Int = 0,
    onNavigate: (HomeNavDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAppInfo: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage != null) {
            delay(3_500L)
            viewModel.onClearStatusMessage()
        }
    }

    HomeScreen(
        uiState = uiState,
        homePressTick = homePressTick,
        onNavigate = onNavigate,
        onRequestDefaultLauncher = onRequestDefaultLauncher,
        onAppFocused = viewModel::onAppFocused,
        onAppClicked = viewModel::onAppClicked,
        onAppLongClicked = viewModel::onAppLongClicked,
        onToggleFavorite = viewModel::onToggleFavorite,
        onHideApp = viewModel::onHideApp,
        onOpenAppInfo = { app ->
            viewModel.onDismissContextMenu()
            onOpenAppInfo(app.packageName)
        },
        onDismissContextMenu = viewModel::onDismissContextMenu,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    homePressTick: Int = 0,
    onNavigate: (HomeNavDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onAppFocused: (AppInfo) -> Unit,
    onAppClicked: (AppInfo) -> Unit,
    onAppLongClicked: (AppInfo) -> Unit,
    onToggleFavorite: (AppInfo) -> Unit,
    onHideApp: (AppInfo) -> Unit,
    onOpenAppInfo: (AppInfo) -> Unit,
    onDismissContextMenu: () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusMemory = rememberFocusMemoryRegistry()
    val firstCardFocusRequester = remember { FocusRequester() }
    val itemFocusRequesters = remember { mutableStateMapOf<String, FocusRequester>() }
    var hasRequestedInitialFocus by remember { mutableStateOf(false) }

    fun requesterFor(sectionId: String, packageName: String): FocusRequester =
        itemFocusRequesters.getOrPut("$sectionId::$packageName") { FocusRequester() }

    // Request initial focus once apps are loaded so D-pad starts on a visible focus ring
    LaunchedEffect(uiState.allAppsCount) {
        if (!hasRequestedInitialFocus && uiState.allAppsCount > 0) {
            hasRequestedInitialFocus = true
            runCatching { firstCardFocusRequester.requestFocus() }
        }
    }

    // Handle Home button press while already on Home
    LaunchedEffect(homePressTick) {
        if (homePressTick > 0) {
            onDismissContextMenu()
            listState.animateScrollToItem(0)
            runCatching { firstCardFocusRequester.requestFocus() }
        }
    }

    // Restore focus to the exact anchor when context menu closes
    fun dismissMenuAndRestoreFocus() {
        val anchor = focusMemory.popOverlayAnchor()
        onDismissContextMenu()
        if (anchor != null) {
            coroutineScope.launch {
                runCatching {
                    itemFocusRequesters["${anchor.sectionId}::${anchor.itemId}"]?.requestFocus()
                }
            }
        }
    }

    // Never exit the TV Home launcher on Back press; instead return to top or close dialog
    BackHandler(enabled = true) {
        when {
            uiState.contextMenuApp != null -> dismissMenuAndRestoreFocus()
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 -> {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                    runCatching { firstCardFocusRequester.requestFocus() }
                }
            }
        }
    }

    LunmedAmbientCanvas(
        modifier = Modifier.onRemoteKeyCommand { command ->
            when (command) {
                RemoteCommand.MENU -> {
                    val target = uiState.focusedApp
                    if (target != null && uiState.contextMenuApp == null) {
                        focusMemory.pushOverlayAnchor()
                        onAppLongClicked(target)
                        true
                    } else {
                        false
                    }
                }
                RemoteCommand.SETTINGS -> {
                    onNavigate(HomeNavDestination.SETTINGS)
                    true
                }
                else -> false
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(
                activeDestination = HomeNavDestination.HOME,
                networkStatus = uiState.networkStatus,
                capabilities = uiState.capabilities,
                showClock = uiState.preferences.showClock,
                showDeviceIndicators = uiState.preferences.showDeviceIndicators,
                isDefaultLauncher = uiState.defaultLauncherState.isDefaultLauncher,
                canRequestDefaultLauncher = uiState.defaultLauncherState.canRequestRole,
                onNavigate = onNavigate,
                onRequestDefaultLauncher = onRequestDefaultLauncher,
            )

            HomeSpotlightStage(
                focusedApp = uiState.focusedApp,
                totalAppsCount = uiState.allAppsCount,
            )

            Spacer(modifier = Modifier.height(spacing.md))

            if (!uiState.isLoading && uiState.allAppsCount == 0) {
                LunmedEmptyState(
                    title = "No applications found",
                    subtitle = "Install an Android TV or Android application to see it here.",
                )
            } else {
                val firstSectionId = when {
                    uiState.favorites.isNotEmpty() -> "favorites"
                    uiState.recentApps.isNotEmpty() -> "recent"
                    else -> uiState.categories.firstOrNull()?.id
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(spacing.sectionGap),
                ) {
                    if (uiState.favorites.isNotEmpty()) {
                        item(key = "section_favorites") {
                            HomeAppRail(
                                sectionId = "favorites",
                                title = "Favorites",
                                apps = uiState.favorites,
                                cardSize = uiState.preferences.cardSize,
                                isFirstRail = firstSectionId == "favorites",
                                firstCardFocusRequester = firstCardFocusRequester,
                                requesterFor = { pkg -> requesterFor("favorites", pkg) },
                                onAppFocused = { app ->
                                    focusMemory.recordFocus("favorites", app.packageName)
                                    onAppFocused(app)
                                },
                                onAppClicked = onAppClicked,
                                onAppLongClicked = { app ->
                                    focusMemory.recordFocus("favorites", app.packageName)
                                    focusMemory.pushOverlayAnchor()
                                    onAppLongClicked(app)
                                },
                            )
                        }
                    }

                    if (uiState.recentApps.isNotEmpty()) {
                        item(key = "section_recent") {
                            HomeAppRail(
                                sectionId = "recent",
                                title = "Recently Used",
                                apps = uiState.recentApps,
                                cardSize = uiState.preferences.cardSize,
                                isFirstRail = firstSectionId == "recent",
                                firstCardFocusRequester = firstCardFocusRequester,
                                requesterFor = { pkg -> requesterFor("recent", pkg) },
                                onAppFocused = { app ->
                                    focusMemory.recordFocus("recent", app.packageName)
                                    onAppFocused(app)
                                },
                                onAppClicked = onAppClicked,
                                onAppLongClicked = { app ->
                                    focusMemory.recordFocus("recent", app.packageName)
                                    focusMemory.pushOverlayAnchor()
                                    onAppLongClicked(app)
                                },
                            )
                        }
                    }

                    items(
                        items = uiState.categories,
                        key = { "category_${it.id}" },
                    ) { category ->
                        HomeAppRail(
                            sectionId = category.id,
                            title = category.title,
                            apps = category.apps,
                            cardSize = uiState.preferences.cardSize,
                            isFirstRail = firstSectionId == category.id,
                            firstCardFocusRequester = firstCardFocusRequester,
                            requesterFor = { pkg -> requesterFor(category.id, pkg) },
                            onAppFocused = { app ->
                                focusMemory.recordFocus(category.id, app.packageName)
                                onAppFocused(app)
                            },
                            onAppClicked = onAppClicked,
                            onAppLongClicked = { app ->
                                focusMemory.recordFocus(category.id, app.packageName)
                                focusMemory.pushOverlayAnchor()
                                onAppLongClicked(app)
                            },
                        )
                    }
                }
            }
        }

        // Launch error or status toast at bottom
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

        uiState.contextMenuApp?.let { app ->
            AppContextMenuDialog(
                app = app,
                onLaunch = {
                    dismissMenuAndRestoreFocus()
                    onAppClicked(app)
                },
                onToggleFavorite = {
                    onToggleFavorite(app)
                    dismissMenuAndRestoreFocus()
                },
                onHide = {
                    onHideApp(app)
                    dismissMenuAndRestoreFocus()
                },
                onOpenAppInfo = { onOpenAppInfo(app) },
                onDismiss = { dismissMenuAndRestoreFocus() },
            )
        }
    }
}

@Composable
private fun HomeAppRail(
    sectionId: String,
    title: String,
    apps: List<AppInfo>,
    cardSize: CardSizePreset,
    isFirstRail: Boolean,
    firstCardFocusRequester: FocusRequester,
    requesterFor: (String) -> FocusRequester,
    onAppFocused: (AppInfo) -> Unit,
    onAppClicked: (AppInfo) -> Unit,
    onAppLongClicked: (AppInfo) -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = LunmedTheme.typography.headlineSection,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = spacing.overscanHorizontal),
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        LazyRow(
            contentPadding = PaddingValues(horizontal = spacing.overscanHorizontal),
            horizontalArrangement = Arrangement.spacedBy(spacing.cardGap),
            modifier = Modifier.lunmedFocusGroupRestorer(),
        ) {
            items(
                items = apps,
                key = { "$sectionId::${it.packageName}" },
            ) { app ->
                val isFirstItem = isFirstRail && apps.firstOrNull()?.packageName == app.packageName
                val itemRequester = requesterFor(app.packageName)
                val cardModifier = if (isFirstItem) {
                    Modifier
                        .focusRequester(firstCardFocusRequester)
                        .focusRequester(itemRequester)
                } else {
                    Modifier.focusRequester(itemRequester)
                }

                HomeAppCard(
                    app = app,
                    cardSize = cardSize,
                    onFocused = { onAppFocused(app) },
                    onClick = { onAppClicked(app) },
                    onLongClick = { onAppLongClicked(app) },
                    modifier = cardModifier,
                )
            }
        }
    }
}
