package com.lunmed.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.ui.components.LunmedAmbientCanvas
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var capabilityDetector: DeviceCapabilityDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val capabilities = capabilityDetector.detect()

        setContent {
            val prefs by preferencesRepository.preferences
                .collectAsStateWithLifecycle(initialValue = LauncherPreferences())

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
                            text = "Architecture & Design System Ready • API ${capabilities.sdkInt} • ${capabilities.screenWidthPx}×${capabilities.screenHeightPx}",
                            style = LunmedTheme.typography.bodySecondary,
                            color = LunmedTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

