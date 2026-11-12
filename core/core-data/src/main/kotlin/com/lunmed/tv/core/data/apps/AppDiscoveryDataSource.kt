package com.lunmed.tv.core.data.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.lunmed.tv.core.common.dispatchers.Dispatcher
import com.lunmed.tv.core.common.dispatchers.LunmedDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AppDiscoveryDataSource {
    suspend fun queryLeanbackApps(): List<DiscoveredAppEntry>
    suspend fun queryStandardApps(): List<DiscoveredAppEntry>
}

@Singleton
class PackageManagerAppDiscoveryDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(LunmedDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : AppDiscoveryDataSource {

    override suspend fun queryLeanbackApps(): List<DiscoveredAppEntry> = withContext(ioDispatcher) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        queryActivities(intent, isTvApp = true)
    }

    override suspend fun queryStandardApps(): List<DiscoveredAppEntry> = withContext(ioDispatcher) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        queryActivities(intent, isTvApp = false)
    }

    private fun queryActivities(intent: Intent, isTvApp: Boolean): List<DiscoveredAppEntry> {
        val pm = context.packageManager
        val resolveInfos: List<ResolveInfo> = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }
        }.getOrNull() ?: emptyList()

        return resolveInfos.mapNotNull { info ->
            val activityInfo = info?.activityInfo ?: return@mapNotNull null
            val appInfo = activityInfo.applicationInfo ?: return@mapNotNull null
            if (activityInfo.packageName.isNullOrBlank()) return@mapNotNull null
            if (!activityInfo.enabled || !appInfo.enabled) return@mapNotNull null

            val label = runCatching { info.loadLabel(pm).toString() }
                .getOrElse { activityInfo.packageName }

            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

            DiscoveredAppEntry(
                packageName = activityInfo.packageName,
                activityName = activityInfo.name,
                label = label,
                isTvApp = isTvApp,
                isSystemApp = isSystem,
                inferredCategoryId = mapCategory(appInfo, isSystem),
            )
        }
    }

    private fun mapCategory(appInfo: ApplicationInfo, isSystem: Boolean): String {
        @Suppress("DEPRECATION")
        val isLegacyGame = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
        if (isLegacyGame) return "games"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return when (appInfo.category) {
                ApplicationInfo.CATEGORY_GAME -> "games"
                ApplicationInfo.CATEGORY_VIDEO,
                ApplicationInfo.CATEGORY_AUDIO,
                ApplicationInfo.CATEGORY_IMAGE -> "media"
                ApplicationInfo.CATEGORY_PRODUCTIVITY,
                ApplicationInfo.CATEGORY_MAPS,
                ApplicationInfo.CATEGORY_NEWS -> "utilities"
                else -> if (isSystem) "system" else "other"
            }
        }
        return if (isSystem) "system" else "other"
    }
}

