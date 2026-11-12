package com.lunmed.tv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.feature.home.HomeRoute
import com.lunmed.tv.feature.home.HomeViewModel
import com.lunmed.tv.feature.home.components.HomeNavDestination
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var capabilityDetector: DeviceCapabilityDetector

    @Inject
    lateinit var defaultLauncherHelper: DefaultLauncherHelper

    private var homePressTick by mutableStateOf(0)

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
                val homeViewModel: HomeViewModel = hiltViewModel()

                val roleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                ) {
                    homeViewModel.onRefreshLauncherState()
                }

                HomeRoute(
                    viewModel = homeViewModel,
                    onNavigate = { destination ->
                        when (destination) {
                            HomeNavDestination.HOME -> Unit
                            HomeNavDestination.APPS,
                            HomeNavDestination.DEVICE,
                            HomeNavDestination.SETTINGS -> {
                                // Feature screens will be wired in Milestones 05-07
                            }
                        }
                    },
                    onRequestDefaultLauncher = {
                        defaultLauncherHelper.createRequestDefaultLauncherIntent()
                            ?.let { roleLauncher.launch(it) }
                    },
                    onOpenAppInfo = { packageName ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        runCatching { startActivity(intent) }
                    },
                )
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
