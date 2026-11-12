package com.lunmed.tv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import com.lunmed.tv.core.ui.components.LunmedFocusableCard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var capabilityDetector: DeviceCapabilityDetector

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var defaultLauncherHelper: DefaultLauncherHelper

    private var homePressTick by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val capabilities = capabilityDetector.detect()

        setContent {
            val prefs by preferencesRepository.preferences
                .collectAsStateWithLifecycle(initialValue = LauncherPreferences())
            val apps by appRepository.observeApps()
                .collectAsStateWithLifecycle(initialValue = emptyList())

            var launcherState by androidx.compose.runtime.remember(homePressTick) {
                mutableStateOf(defaultLauncherHelper.getState())
            }

            val roleLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
            ) {
                launcherState = defaultLauncherHelper.getState()
            }

            val effectiveAnimation = if (capabilities.isLowRamDevice) {
                capabilities.recommendedAnimationIntensity
            } else {
                prefs.animationIntensity
            }

            LunmedTheme(
                accentPreset = prefs.accentPreset,
                density = prefs.density,
                animationIntensity = effectiveAnimation,
            ) {
                LunmedAmbientCanvas {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(LunmedTheme.spacing.overscanHorizontal),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "LUNMED TV",
                            style = LunmedTheme.typography.displayHero,
                            color = LunmedTheme.colors.textPrimary,
                        )
                        Spacer(modifier = Modifier.height(LunmedTheme.spacing.sm))
                        Text(
                            text = "Apps Discovered: ${apps.size} • Default Launcher: ${if (launcherState.isDefaultLauncher) "Active" else "Not Set"} • API ${capabilities.sdkInt}",
                            style = LunmedTheme.typography.bodySecondary,
                            color = LunmedTheme.colors.textSecondary,
                        )
                        if (!launcherState.isDefaultLauncher && launcherState.canRequestRole) {
                            Spacer(modifier = Modifier.height(LunmedTheme.spacing.lg))
                            Row {
                                LunmedFocusableCard(
                                    onClick = {
                                        defaultLauncherHelper.createRequestDefaultLauncherIntent()
                                            ?.let { roleLauncher.launch(it) }
                                    },
                                ) {
                                    Text(
                                        text = "Set LUNMED TV as Default Launcher",
                                        style = LunmedTheme.typography.labelAction,
                                        color = LunmedTheme.colors.accentPrimary,
                                        modifier = Modifier.padding(
                                            horizontal = LunmedTheme.spacing.lg,
                                            vertical = LunmedTheme.spacing.md,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.hasCategory(Intent.CATEGORY_HOME)) {
            homePressTick++
        }
    }
}
