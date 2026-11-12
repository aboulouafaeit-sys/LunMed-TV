package com.lunmed.tv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LauncherPreferences
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.feature.apps.AppLibraryRoute
import com.lunmed.tv.feature.device.DeviceCenterRoute
import com.lunmed.tv.feature.home.HomeRoute
import com.lunmed.tv.feature.home.HomeViewModel
import com.lunmed.tv.feature.settings.SettingsRoute
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

            val isArabic = when (prefs.languageTag) {
                "ar" -> true
                "system" -> java.util.Locale.getDefault().language == "ar"
                else -> false
            }
            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            LaunchedEffect(prefs.languageTag) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    val localeManager = getSystemService(android.app.LocaleManager::class.java)
                    val locales = if (prefs.languageTag == "system") {
                        android.os.LocaleList.getEmptyLocaleList()
                    } else {
                        android.os.LocaleList.forLanguageTags(prefs.languageTag)
                    }
                    runCatching { localeManager.applicationLocales = locales }
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                LunmedTheme(
                    accentPreset = prefs.accentPreset,
                    density = prefs.density,
                    animationIntensity = effectiveAnimation,
                ) {
                    var currentDestination by rememberSaveable { mutableStateOf(LunmedDestination.HOME) }

                LaunchedEffect(homePressTick) {
                    if (homePressTick > 0 && currentDestination != LunmedDestination.HOME) {
                        currentDestination = LunmedDestination.HOME
                    }
                }

                val roleLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                ) {
                    // Handled inside ViewModels
                }

                fun openAppInfo(packageName: String) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    runCatching { startActivity(intent) }
                }

                fun requestDefaultLauncher() {
                    defaultLauncherHelper.createRequestDefaultLauncherIntent()
                        ?.let { roleLauncher.launch(it) }
                }

                val anim = LunmedTheme.animation
                val transitionSpec = anim.transitionSpec<Float>()
                val focusSpec = anim.focusSpec<Float>()

                AnimatedContent(
                    targetState = currentDestination,
                    transitionSpec = {
                        fadeIn(animationSpec = transitionSpec)
                            .togetherWith(fadeOut(animationSpec = focusSpec))
                    },
                    label = "MainScreenDestinationTransition",
                ) { destination ->
                    fun handleNavigate(destination: LunmedDestination) {
                        currentDestination = destination
                    }

                    fun openAndroidSettings() {
                        val intent = Intent(Settings.ACTION_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        runCatching { startActivity(intent) }
                    }

                    fun openAccessibilitySettings() {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        runCatching { startActivity(intent) }
                    }

                    when (destination) {
                        LunmedDestination.HOME -> {
                            val homeViewModel: HomeViewModel = hiltViewModel()
                            HomeRoute(
                                homePressTick = homePressTick,
                                viewModel = homeViewModel,
                                onNavigate = ::handleNavigate,
                                onRequestDefaultLauncher = ::requestDefaultLauncher,
                                onOpenAppInfo = ::openAppInfo,
                            )
                        }
                        LunmedDestination.APPS -> {
                            AppLibraryRoute(
                                onNavigate = ::handleNavigate,
                                onRequestDefaultLauncher = ::requestDefaultLauncher,
                                onOpenAppInfo = ::openAppInfo,
                            )
                        }
                        LunmedDestination.SETTINGS -> {
                            SettingsRoute(
                                onNavigate = ::handleNavigate,
                                onRequestDefaultLauncher = ::requestDefaultLauncher,
                                onOpenAndroidSettings = ::openAndroidSettings,
                                onOpenAccessibilitySettings = ::openAccessibilitySettings,
                            )
                        }
                        LunmedDestination.DEVICE -> {
                            DeviceCenterRoute(
                                onNavigate = ::handleNavigate,
                                onRequestDefaultLauncher = ::requestDefaultLauncher,
                                onOpenAndroidSettings = ::openAndroidSettings,
                            )
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
        if (intent.hasCategory(Intent.CATEGORY_HOME) || intent.action == Intent.ACTION_MAIN) {
            homePressTick++
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == android.view.KeyEvent.KEYCODE_HOME) {
            homePressTick++
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
