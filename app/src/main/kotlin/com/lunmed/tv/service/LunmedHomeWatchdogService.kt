package com.lunmed.tv.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.lunmed.tv.MainActivity

/**
 * High-performance, lightweight accessibility watchdog service.
 * Intercepts HOME button key events and vendor stock launcher takeovers
 * on restricted TV Box ROMs (e.g. Amlogic, Rockchip, Allwinner, FireOS).
 */
class LunmedHomeWatchdogService : AccessibilityService() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastInterceptEpochMs = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val targetPackage = event.packageName?.toString() ?: return
        if (targetPackage == packageName) return

        // Intercept known OEM vendor stock launchers on restricted boxes
        if (isKnownVendorStockLauncher(targetPackage)) {
            val now = System.currentTimeMillis()
            if (now - lastInterceptEpochMs > DEBOUNCE_WINDOW_MS) {
                lastInterceptEpochMs = now
                bringLunmedToForeground()
            }
        }
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_HOME -> {
                    bringLunmedToForeground()
                    return true
                }
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
        // Required lifecycle callback
    }

    private fun bringLunmedToForeground() {
        mainHandler.post {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION,
                )
            }
            runCatching { startActivity(intent) }
        }
    }

    companion object {
        private const val DEBOUNCE_WINDOW_MS = 600L

        private val KNOWN_VENDOR_LAUNCHERS = setOf(
            // Google / Android TV stock
            "com.google.android.tvlauncher",
            "com.google.android.tungsten.setupwraith",
            // Amlogic standard & TV box OEM launchers
            "com.droidlogic.tvlauncher",
            "com.droidlogic.launcher",
            "com.droidlogic.appinstall",
            // Rockchip OEM launchers
            "com.rockchip.launcher",
            "com.rockchip.devicetest",
            // Allwinner OEM launchers
            "com.allwinner.launcher",
            // Fire TV / Amazon OS
            "com.amazon.tv.launcher",
            // Xiaomi PatchWall / MiTV
            "com.mitv.tvhome",
            // Generic Chinese STB / OTT launchers
            "com.mstar.tv.launcher",
            "com.realtek.launcher",
            "com.hisilicon.launcher",
        )

        fun isKnownVendorStockLauncher(packageName: String): Boolean =
            packageName in KNOWN_VENDOR_LAUNCHERS
    }
}

