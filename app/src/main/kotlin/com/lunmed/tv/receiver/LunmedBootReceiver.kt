package com.lunmed.tv.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lunmed.tv.MainActivity

/**
 * Ensures LUNMED TV launches on device boot across diverse hardware
 * including cold boot, fast boot (QuickBoot), and Direct Boot mode.
 */
class LunmedBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action
        if (action in BOOT_ACTIONS) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
                )
            }
            runCatching { context.startActivity(launchIntent) }
        }
    }

    companion object {
        val BOOT_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_REBOOT,
        )
    }
}

