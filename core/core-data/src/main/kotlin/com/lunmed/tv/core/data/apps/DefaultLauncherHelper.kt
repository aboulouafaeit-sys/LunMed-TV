package com.lunmed.tv.core.data.apps

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.admin.DevicePolicyManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.lunmed.tv.core.model.DefaultLauncherState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks and requests the default HOME launcher role using official Android mechanisms.
 */
interface DefaultLauncherHelper {
    fun getState(): DefaultLauncherState
    fun createRequestDefaultLauncherIntent(): Intent?

    /**
     * Intent that promotes the app to an active device administrator, the prerequisite for the
     * immutable persistent HOME preference. Returns null when already admin / device owner.
     */
    fun createDeviceAdminRequestIntent(): Intent? = null
}

@Singleton
class AndroidDefaultLauncherHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : DefaultLauncherHelper {

    override fun getState(): DefaultLauncherState {
        val roleHeld = isHomeRoleHeld()
        val resolvedDefault = resolveDefaultHomePackage()
        val isDefault = roleHeld || resolvedDefault == context.packageName
        val currentDefault = when {
            resolvedDefault != null -> resolvedDefault
            roleHeld -> context.packageName
            else -> null
        }
        val canRequestRole = canUseRoleManager() || resolveHomeSettingsIntent() != null
        return DefaultLauncherState(
            isDefaultLauncher = isDefault,
            currentDefaultPackage = currentDefault,
            canRequestRole = canRequestRole,
            isWatchdogEnabled = isWatchdogServiceEnabled(),
            isDeviceAdminActive = isDeviceAdminActive(),
            isDeviceOwner = isDeviceOwner(),
        )
    }

    override fun createRequestDefaultLauncherIntent(): Intent? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            if (roleManager != null &&
                roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_HOME)
            ) {
                return roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
            }
        }
        return resolveHomeSettingsIntent()
    }

    override fun createDeviceAdminRequestIntent(): Intent? {
        if (isDeviceAdminActive() || isDeviceOwner()) return null
        return runCatching {
            Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent())
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Activates the persistent enterprise Home lockdown so LUNMED TV keeps the " +
                        "HOME role after reboots and cannot be replaced by the vendor launcher.",
                )
            }
        }.getOrNull()
    }

    private fun isHomeRoleHeld(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val roleManager = context.getSystemService(RoleManager::class.java) ?: return false
        return runCatching {
            roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        }.getOrDefault(false)
    }

    private fun canUseRoleManager(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return false
        val roleManager = context.getSystemService(RoleManager::class.java) ?: return false
        return roleManager.isRoleAvailable(RoleManager.ROLE_HOME)
    }

    private fun resolveHomeSettingsIntent(): Intent? {
        val settingsIntent = Intent(Settings.ACTION_HOME_SETTINGS)
        if (settingsIntent.resolveActivity(context.packageManager) != null) {
            return settingsIntent
        }
        val chooserIntent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_HOME)
        return if (chooserIntent.resolveActivity(context.packageManager) != null) {
            Intent.createChooser(chooserIntent, null)
        } else {
            null
        }
    }

    private fun isWatchdogServiceEnabled(): Boolean {
        val selfPackage = context.packageName
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        val boundServices = runCatching {
            accessibilityManager?.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK,
            )
        }.getOrNull().orEmpty()

        for (service in boundServices) {
            val serviceInfo = service.resolveInfo?.serviceInfo ?: continue
            if (serviceInfo.packageName == selfPackage &&
                serviceInfo.name.orEmpty().contains(WATCHDOG_SERVICE_CLASS_SIMPLE_NAME)
            ) {
                return true
            }
        }

        // Fallback for ROMs that report an empty AccessibilityManager list.
        val enabledSetting = runCatching {
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            )
        }.getOrNull().orEmpty()

        return enabledSetting.split(':').any { entry ->
            entry.startsWith("$selfPackage/") && entry.contains(WATCHDOG_SERVICE_CLASS_SIMPLE_NAME)
        }
    }

    private fun deviceAdminComponent(): ComponentName =
        ComponentName(context.packageName, DEVICE_ADMIN_RECEIVER_CLASS_NAME)

    private fun isDeviceAdminActive(): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            ?: return false
        return runCatching { dpm.isAdminActive(deviceAdminComponent()) }.getOrDefault(false)
    }

    private fun isDeviceOwner(): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            ?: return false
        return runCatching { dpm.isDeviceOwnerApp(context.packageName) }.getOrDefault(false)
    }

    private fun resolveDefaultHomePackage(): String? {
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
        val resolvedPackage = resolveInfo?.activityInfo?.packageName ?: return null
        // When no default Home is chosen Android reports the system ResolverActivity ("android").
        // Treat that as "no default" instead of a competing launcher.
        return resolvedPackage.takeUnless { it == SYSTEM_RESOLVER_PACKAGE }
    }

    private companion object {
        const val WATCHDOG_SERVICE_CLASS_SIMPLE_NAME = "LunmedHomeWatchdogService"
        const val DEVICE_ADMIN_RECEIVER_CLASS_NAME =
            "com.lunmed.tv.receiver.LunmedDeviceAdminReceiver"
        const val SYSTEM_RESOLVER_PACKAGE = "android"
    }
}

