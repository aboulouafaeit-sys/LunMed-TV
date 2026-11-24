package com.lunmed.tv.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
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
    private var lastSelfVisibleEpochMs = 0L

    private val dynamicLauncherPackages = HashSet<String>()
    private var lastLauncherScanEpochMs = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Re-apply the runtime flags on every connection. Some vendor ROMs keep stale stored
        // flags from an earlier install, which silently disables KEYCODE_HOME filtering - the
        // classic "accessibility enabled but Home key is not intercepted" failure.
        runCatching {
            val info = serviceInfo ?: AccessibilityServiceInfo()
            info.eventTypes = info.eventTypes or AccessibilityEvent.TYPE_WINDOWS_CHANGED
            info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.notificationTimeout = NOTIFICATION_TIMEOUT_MS
            setServiceInfo(info)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val targetPackage = event.packageName?.toString() ?: return
        if (targetPackage == packageName) {
            // Our own window is in front: remember it so a Home press is handled by
            // MainActivity.onKeyDown (returns to the Home tab) instead of restarting the task.
            lastSelfVisibleEpochMs = System.currentTimeMillis()
            return
        }

        // Intercept known OEM vendor stock launchers or any detected 3rd-party launcher on restricted boxes
        if (isTargetLauncher(targetPackage)) {
            val now = System.currentTimeMillis()
            if (now - lastInterceptEpochMs > DEBOUNCE_WINDOW_MS) {
                lastInterceptEpochMs = now
                bringLunmedToForeground()
            }
        }
    }

    private fun isTargetLauncher(targetPackage: String): Boolean {
        if (targetPackage == packageName || targetPackage == SYSTEM_PACKAGE) return false
        if (isKnownVendorStockLauncher(targetPackage)) return true
        return isDynamicHomeLauncher(targetPackage)
    }

    private fun isDynamicHomeLauncher(targetPackage: String): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastLauncherScanEpochMs > DYNAMIC_SCAN_INTERVAL_MS || dynamicLauncherPackages.isEmpty()) {
            lastLauncherScanEpochMs = now
            val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val homeActivities = runCatching {
                packageManager.queryIntentActivities(homeIntent, 0)
            }.getOrNull() ?: emptyList()

            dynamicLauncherPackages.clear()
            for (resolve in homeActivities) {
                val pkg = resolve.activityInfo?.packageName
                if (!pkg.isNullOrBlank() && pkg != packageName && pkg != SYSTEM_PACKAGE) {
                    dynamicLauncherPackages.add(pkg)
                }
            }
        }
        return targetPackage in dynamicLauncherPackages
    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_HOME -> {
                    val now = System.currentTimeMillis()
                    // Already showing our launcher: return false so MainActivity.onKeyDown gets
                    // the press and navigates back to the Home tab (no task restart flicker).
                    if (now - lastSelfVisibleEpochMs < SELF_VISIBLE_WINDOW_MS) return false
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
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_HOME)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                        Intent.FLAG_ACTIVITY_NO_ANIMATION,
                )
            }
            runCatching { startActivity(intent) }
            lastSelfVisibleEpochMs = System.currentTimeMillis()
        }
    }

    companion object {
        private const val DEBOUNCE_WINDOW_MS = 600L
        private const val DYNAMIC_SCAN_INTERVAL_MS = 60_000L
        private const val SELF_VISIBLE_WINDOW_MS = 1_500L
        private const val NOTIFICATION_TIMEOUT_MS = 50L
        private const val SYSTEM_PACKAGE = "android"

        private val KNOWN_VENDOR_LAUNCHERS = setOf(
            // Google / Android TV stock
            "com.google.android.tvlauncher",
            "com.google.android.apps.tv.launcherx",
            "com.google.android.tungsten.setupwraith",
            "com.google.android.leanbacklauncher",
            // Amlogic standard & TV box OEM launchers
            "com.droidlogic.tvlauncher",
            "com.droidlogic.launcher",
            "com.droidlogic.appinstall",
            "com.alber.tvlauncher",
            "com.elinkway.tvlauncher",
            "com.t95.launcher",
            "com.x96.launcher",
            "com.tanix.launcher",
            "com.ugoos.launcher",
            "com.minix.launcher",
            // Rockchip OEM launchers
            "com.rockchip.launcher",
            "com.rockchip.devicetest",
            // Allwinner & Softwinner OEM launchers
            "com.allwinner.launcher",
            "com.allwinner.homelauncher",
            "com.softwinner.launcher",
            "com.softwinner.unilauncher",
            "com.shashlik.launcher",
            // Fire TV / Amazon OS
            "com.amazon.tv.launcher",
            // Xiaomi PatchWall / MiTV
            "com.mitv.tvhome",
            // Generic Chinese STB / OTT launchers
            "com.mstar.tv.launcher",
            "com.realtek.launcher",
            "com.hisilicon.launcher",
            "com.himedia.launcher",
            "com.zidoo.launcher",
            "com.zidoo.poster",
            "com.skyworth.tvos.launcher",
            "com.tcl.tv.launcher",
            "com.tcl.homelauncher",
            "com.hisense.tv.launcher",
            "com.huawei.android.tvlauncher",
            "com.zte.browser.launcher",
            "com.zte.iptvclient.android.launcher",
            "com.fh.launcher",
            "com.inphic.launcher",
            "com.sunniwell.launcher",
            "com.yunos.tv.homeshell",
            "com.dangbei.dbdesktop",
            "com.dangbei.tvlauncher",
            "com.formuler.mol2",
            "com.formuler.mol3",
            "com.formuler.launcher",
            // Stock AOSP launchers present in generic ROMs
            "com.android.launcher3",
            "com.android.launcher2",
            "com.cyanogenmod.trebuchet",
        )

        fun isKnownVendorStockLauncher(packageName: String): Boolean =
            packageName in KNOWN_VENDOR_LAUNCHERS
    }
}

