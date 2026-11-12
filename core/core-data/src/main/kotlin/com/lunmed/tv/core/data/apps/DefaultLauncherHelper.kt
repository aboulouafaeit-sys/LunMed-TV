package com.lunmed.tv.core.data.apps

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
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
}

@Singleton
class AndroidDefaultLauncherHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : DefaultLauncherHelper {

    override fun getState(): DefaultLauncherState {
        val currentDefault = resolveDefaultHomePackage()
        val isDefault = currentDefault == context.packageName
        val canRequestRole = canUseRoleManager() || resolveHomeSettingsIntent() != null
        return DefaultLauncherState(
            isDefaultLauncher = isDefault,
            currentDefaultPackage = currentDefault,
            canRequestRole = canRequestRole,
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
        return resolveInfo?.activityInfo?.packageName
    }
}
