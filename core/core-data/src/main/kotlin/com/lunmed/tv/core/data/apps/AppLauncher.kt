package com.lunmed.tv.core.data.apps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.lunmed.tv.core.model.LaunchResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface AppLauncher {
    fun launch(packageName: String): LaunchResult
}

@Singleton
class AndroidAppLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppLauncher {

    override fun launch(packageName: String): LaunchResult {
        val pm = context.packageManager
        val launchIntent = pm.getLeanbackLaunchIntentForPackage(packageName)
            ?: pm.getLaunchIntentForPackage(packageName)
            ?: return LaunchResult.PackageUnavailable(packageName)

        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
        )

        return try {
            context.startActivity(launchIntent)
            LaunchResult.Success
        } catch (_: ActivityNotFoundException) {
            LaunchResult.PackageUnavailable(packageName)
        } catch (_: SecurityException) {
            LaunchResult.PermissionDenied(packageName)
        } catch (t: Throwable) {
            LaunchResult.Failed(packageName, t.message ?: "Unknown launch error")
        }
    }
}
