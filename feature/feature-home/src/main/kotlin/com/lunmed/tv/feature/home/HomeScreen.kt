package com.lunmed.tv.feature.home

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedEmptyState
import com.lunmed.tv.feature.home.components.AppContextMenuDialog
import com.lunmed.tv.feature.home.components.HomeAppCard
import com.lunmed.tv.feature.home.components.HomeNavDestination
import com.lunmed.tv.feature.home.components.HomeSpotlightStage
import com.lunmed.tv.feature.home.components.HomeTopBar
import kotlinx.coroutines.delay

@Composable
fun HomeRoute(
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

    LunmedAmbientCanvas {
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(spacing.sectionGap),
                ) {
                    if (uiState.favorites.isNotEmpty()) {
                        item(key = "section_favorites") {
                            HomeAppRail(
                                title = "Favorites",
                                apps = uiState.favorites,
                                cardSize = uiState.preferences.cardSize,
                                onAppFocused = onAppFocused,
                                onAppClicked = onAppClicked,
                                onAppLongClicked = onAppLongClicked,
                            )
                        }
                    }

                    if (uiState.recentApps.isNotEmpty()) {
                        item(key = "section_recent") {
                            HomeAppRail(
                                title = "Recently Used",
                                apps = uiState.recentApps,
                                cardSize = uiState.preferences.cardSize,
                                onAppFocused = onAppFocused,
                                onAppClicked = onAppClicked,
                                onAppLongClicked = onAppLongClicked,
                            )
                        }
                    }

                    items(
                        items = uiState.categories,
                        key = { "category_${it.id}" },
                    ) { category ->
                        HomeAppRail(
                            title = category.title,
                            apps = category.apps,
                            cardSize = uiState.preferences.cardSize,
                            onAppFocused = onAppFocused,
                            onAppClicked = onAppClicked,
                            onAppLongClicked = onAppLongClicked,
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
                    onDismissContextMenu()
                    onAppClicked(app)
                },
                onToggleFavorite = { onToggleFavorite(app) },
                onHide = { onHideApp(app) },
                onOpenAppInfo = { onOpenAppInfo(app) },
                onDismiss = onDismissContextMenu,
            )
        }
    }
}

@Composable
private fun HomeAppRail(
    title: String,
    apps: List<AppInfo>,
    cardSize: CardSizePreset,
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
        ) {
            items(
                items = apps,
                key = { it.packageName },
            ) { app ->
                HomeAppCard(
                    app = app,
                    cardSize = cardSize,
                    onFocused = { onAppFocused(app) },
                    onClick = { onAppClicked(app) },
                    onLongClick = { onAppLongClicked(app) },
                )
            }
        }
    }
}

