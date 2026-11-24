package com.lunmed.tv.receiver

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.lunmed.tv.MainActivity

/**
 * Enterprise & Deep-ROM Device Admin / Device Owner receiver.
 *
 * When provisioned as Device Owner (`dpm set-device-owner com.lunmed.tv/.receiver.LunmedDeviceAdminReceiver`),
 * this receiver enables Android's native `addPersistentPreferredActivity` hook.
 * This instructs the Android OS kernel and ActivityManagerService to treat LUNMED TV
 * as the permanent, immutable factory default launcher without prompts, across all reboots.
 */
class LunmedDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        lockHomeToLunmed(context)
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        lockHomeToLunmed(context)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        // Leaving a persistent preferred activity behind would soft-brick Home on some ROMs
        // once the admin is gone, so always release the pin explicitly.
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        runCatching {
            dpm?.clearPackagePersistentPreferredActivities(
                getComponentName(context),
                context.packageName,
            )
        }
    }

    companion object {
        const val RECEIVER_CLASS_NAME = "com.lunmed.tv.receiver.LunmedDeviceAdminReceiver"

        fun getComponentName(context: Context): ComponentName =
            ComponentName(context.packageName, RECEIVER_CLASS_NAME)

        fun isDeviceOwner(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            return dpm.isDeviceOwnerApp(context.packageName)
        }

        fun lockHomeToLunmed(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
                ?: return false
            val adminComponent = getComponentName(context)

            if (!dpm.isDeviceOwnerApp(context.packageName)) {
                return false
            }

            val filter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            val activityComponent = ComponentName(context, MainActivity::class.java)

            return runCatching {
                dpm.addPersistentPreferredActivity(adminComponent, filter, activityComponent)
                true
            }.getOrDefault(false)
        }
    }
}
