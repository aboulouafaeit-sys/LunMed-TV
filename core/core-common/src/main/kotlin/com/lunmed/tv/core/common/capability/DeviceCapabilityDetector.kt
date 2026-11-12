package com.lunmed.tv.core.common.capability

import android.app.ActivityManager
import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import com.lunmed.tv.core.model.DeviceCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Detects real hardware and OS capabilities on the host Android TV / AOSP TV Box.
 * Never assumes hardware features; always queries public Android system services.
 */
interface DeviceCapabilityDetector {
    fun detect(): DeviceCapabilities
}

@Singleton
class AndroidDeviceCapabilityDetector @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceCapabilityDetector {

    override fun detect(): DeviceCapabilities {
        val pm = context.packageManager
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val config = context.resources.configuration

        val isLeanback = pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        val isTvUiMode = uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
        val hasTouchscreen = config.touchscreen != Configuration.TOUCHSCREEN_NOTOUCH
        val hasDpad = config.navigation == Configuration.NAVIGATION_DPAD ||
            config.navigation == Configuration.NAVIGATION_UNDEFINED && isLeanback
        val isLowRam = activityManager?.isLowRamDevice ?: false

        val (widthPx, heightPx, densityDpi) = readDisplayMetrics(context)

        return DeviceCapabilities(
            sdkInt = Build.VERSION.SDK_INT,
            isLeanbackSupported = isLeanback,
            isTelevisionUiMode = isTvUiMode,
            hasTouchscreen = hasTouchscreen,
            hasDpad = hasDpad,
            isLowRamDevice = isLowRam,
            screenWidthPx = widthPx,
            screenHeightPx = heightPx,
            densityDpi = densityDpi,
        )
    }

    private fun readDisplayMetrics(context: Context): Triple<Int, Int, Int> {
        val densityDpi = context.resources.displayMetrics.densityDpi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val wm = context.getSystemService(WindowManager::class.java)
            if (wm != null) {
                val bounds = wm.currentWindowMetrics.bounds
                return Triple(bounds.width(), bounds.height(), densityDpi)
            }
        }
        val metrics = context.resources.displayMetrics
        return Triple(metrics.widthPixels, metrics.heightPixels, densityDpi)
    }
}

