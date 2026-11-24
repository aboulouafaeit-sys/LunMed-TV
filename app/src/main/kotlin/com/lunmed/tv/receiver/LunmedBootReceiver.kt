package com.lunmed.tv.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import com.lunmed.tv.MainActivity

/**
 * Ensures LUNMED TV launches on device boot across diverse hardware
 * including cold boot, fast boot (QuickBoot), and Direct Boot mode.
 *
 * The launcher only claims the screen when Android already resolves LUNMED TV as the default
 * HOME activity (or when no HOME activity exists at all). This avoids fighting the launcher the
 * user deliberately selected and avoids the background-activity-start limits of Android 10+.
 */
class LunmedBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val receivedAction = intent.action ?: return
        if (receivedAction !in BOOT_ACTIONS) return

        // Direct Boot: preferences/Room live in credential-encrypted storage, so starting the
        // launcher before the user is unlocked would crash. BOOT_COMPLETED is delivered later.
        if (receivedAction == Intent.ACTION_LOCKED_BOOT_COMPLETED && !isUserUnlocked(context)) {
            return
        }

        if (!shouldShowLauncher(context)) return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            setAction(Intent.ACTION_MAIN)
            addCategory(Intent.CATEGORY_HOME)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            )
        }
        runCatching { context.startActivity(launchIntent) }
    }

    private fun isUserUnlocked(context: Context): Boolean {
        val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
        return userManager?.isUserUnlocked ?: true
    }

    private fun shouldShowLauncher(context: Context): Boolean {
        // Device owner: re-assert the persistent preferred Home activity on every boot.
        // This is the only OS-level hook that survives reboot and cannot be replaced.
        if (LunmedDeviceAdminReceiver.isDeviceOwner(context)) {
            LunmedDeviceAdminReceiver.lockHomeToLunmed(context)
        }

        val defaultHomePackage = resolveDefaultHomePackage(context) ?: return true
        // Never steal the screen from a launcher the user deliberately chose.
        return defaultHomePackage == context.packageName
    }

    private fun resolveDefaultHomePackage(context: Context): String? {
        val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val pm = context.packageManager
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(
                homeIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            pm.resolveActivity(homeIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return resolveInfo?.activityInfo?.packageName
            ?.takeUnless { it == SYSTEM_RESOLVER_PACKAGE }
    }

    companion object {
        private const val SYSTEM_RESOLVER_PACKAGE = "android"

        val BOOT_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
        )
    }
}

