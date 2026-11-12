package com.lunmed.tv.feature.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.core.navigation.remote.RemoteCommand
import com.lunmed.tv.core.navigation.remote.onRemoteKeyCommand
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedTopBar
import com.lunmed.tv.feature.settings.components.ResetConfirmationDialog
import com.lunmed.tv.feature.settings.components.SettingsDetailPane
import com.lunmed.tv.feature.settings.components.SettingsNavSidebar
import com.lunmed.tv.feature.settings.model.SettingsSection
import kotlinx.coroutines.delay

@Composable
fun SettingsRoute(
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAndroidSettings: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.statusMessage) {
        if (uiState.statusMessage != null) {
            delay(3_500L)
            viewModel.onClearStatusMessage()
        }
    }

    SettingsScreen(
        uiState = uiState,
        onNavigate = onNavigate,
        onRequestDefaultLauncher = onRequestDefaultLauncher,
        onOpenAndroidSettings = onOpenAndroidSettings,
        onOpenAccessibilitySettings = onOpenAccessibilitySettings,
        onSelectSection = viewModel::onSelectSection,
        onSetAccentPreset = viewModel::onSetAccentPreset,
        onSetDensity = viewModel::onSetDensity,
        onSetCardSize = viewModel::onSetCardSize,
        onSetAnimationIntensity = viewModel::onSetAnimationIntensity,
        onToggleShowClock = viewModel::onToggleShowClock,
        onToggleShowDeviceIndicators = viewModel::onToggleShowDeviceIndicators,
        onToggleShowRecentApps = viewModel::onToggleShowRecentApps,
        onToggleShowStartupSplash = viewModel::onToggleShowStartupSplash,
        onEnforceRootDefault = viewModel::onEnforceRootPermanentDefault,
        onRevertRootDefault = viewModel::onRevertRootEnforcement,
        onSetLanguageTag = viewModel::onSetLanguageTag,
        onOpenResetConfirmation = viewModel::onOpenResetConfirmation,
        onDismissResetConfirmation = viewModel::onDismissResetConfirmation,
        onConfirmResetToDefaults = viewModel::onConfirmResetToDefaults,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onNavigate: (LunmedDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    onOpenAndroidSettings: () -> Unit,
    onOpenAccessibilitySettings: () -> Unit,
    onSelectSection: (SettingsSection) -> Unit,
    onSetAccentPreset: (com.lunmed.tv.core.model.AccentPreset) -> Unit,
    onSetDensity: (com.lunmed.tv.core.model.InterfaceDensity) -> Unit,
    onSetCardSize: (com.lunmed.tv.core.model.CardSizePreset) -> Unit,
    onSetAnimationIntensity: (com.lunmed.tv.core.model.AnimationIntensity) -> Unit,
    onToggleShowClock: (Boolean) -> Unit,
    onToggleShowDeviceIndicators: (Boolean) -> Unit,
    onToggleShowRecentApps: (Boolean) -> Unit,
    onToggleShowStartupSplash: (Boolean) -> Unit = {},
    onEnforceRootDefault: () -> Unit = {},
    onRevertRootDefault: () -> Unit = {},
    onSetLanguageTag: (String) -> Unit,
    onOpenResetConfirmation: () -> Unit,
    onDismissResetConfirmation: () -> Unit,
    onConfirmResetToDefaults: () -> Unit,
) {
    val spacing = LunmedTheme.spacing
    val colors = LunmedTheme.colors

    BackHandler(enabled = true) {
        if (uiState.isResetConfirmationOpen) {
            onDismissResetConfirmation()
        } else {
            onNavigate(LunmedDestination.HOME)
        }
    }

    LunmedAmbientCanvas(
        modifier = Modifier.onRemoteKeyCommand { command ->
            when (command) {
                RemoteCommand.SETTINGS -> {
                    onNavigate(LunmedDestination.HOME)
                    true
                }
                RemoteCommand.SEARCH -> {
                    onNavigate(LunmedDestination.APPS)
                    true
                }
                else -> false
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LunmedTopBar(
                activeDestination = LunmedDestination.SETTINGS,
                networkStatus = uiState.networkStatus,
                capabilities = uiState.capabilities,
                showClock = uiState.preferences.showClock,
                showDeviceIndicators = uiState.preferences.showDeviceIndicators,
                isDefaultLauncher = uiState.defaultLauncherState.isDefaultLauncher,
                canRequestDefaultLauncher = uiState.defaultLauncherState.canRequestRole,
                onNavigate = onNavigate,
                onRequestDefaultLauncher = onRequestDefaultLauncher,
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.overscanHorizontal),
            ) {
                SettingsNavSidebar(
                    selectedSection = uiState.selectedSection,
                    onSelectSection = onSelectSection,
                )

                SettingsDetailPane(
                    section = uiState.selectedSection,
                    preferences = uiState.preferences,
                    capabilities = uiState.capabilities,
                    defaultLauncherState = uiState.defaultLauncherState,
                    rootState = uiState.rootState,
                    onSetAccentPreset = onSetAccentPreset,
                    onSetDensity = onSetDensity,
                    onSetCardSize = onSetCardSize,
                    onSetAnimationIntensity = onSetAnimationIntensity,
                    onToggleShowClock = onToggleShowClock,
                    onToggleShowDeviceIndicators = onToggleShowDeviceIndicators,
                    onToggleShowRecentApps = onToggleShowRecentApps,
                    onToggleShowStartupSplash = onToggleShowStartupSplash,
                    onRequestDefaultLauncher = onRequestDefaultLauncher,
                    onOpenAndroidSettings = onOpenAndroidSettings,
                    onOpenAccessibilitySettings = onOpenAccessibilitySettings,
                    onEnforceRootDefault = onEnforceRootDefault,
                    onRevertRootDefault = onRevertRootDefault,
                    onSetLanguageTag = onSetLanguageTag,
                    onOpenResetConfirmation = onOpenResetConfirmation,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Status Toast
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
                    color = colors.statusSuccess,
                )
            }
        }

        // Reset Confirmation Dialog
        if (uiState.isResetConfirmationOpen) {
            ResetConfirmationDialog(
                onConfirm = onConfirmResetToDefaults,
                onDismiss = onDismissResetConfirmation,
            )
        }
    }
}

