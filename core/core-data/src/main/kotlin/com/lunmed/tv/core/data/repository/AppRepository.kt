package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.common.dispatchers.Dispatcher
import com.lunmed.tv.core.common.dispatchers.LunmedDispatcher
import com.lunmed.tv.core.data.apps.AppDiscoveryDataSource
import com.lunmed.tv.core.data.apps.AppDiscoveryMerger
import com.lunmed.tv.core.data.apps.AppLauncher
import com.lunmed.tv.core.data.apps.PackageChangeEvent
import com.lunmed.tv.core.data.apps.PackageChangeMonitor
import com.lunmed.tv.core.database.dao.AppMetadataDao
import com.lunmed.tv.core.database.entity.AppMetadataEntity
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.LaunchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface AppRepository {
    fun observeApps(includeHidden: Boolean = false): Flow<List<AppInfo>>
    suspend fun refresh()
    suspend fun launchApp(packageName: String): LaunchResult
    suspend fun toggleFavorite(packageName: String)
    suspend fun setHidden(packageName: String, hidden: Boolean)
    suspend fun updateCustomOrder(orderedPackages: List<String>)
    suspend fun setCategory(packageName: String, categoryId: String?)
}

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class DefaultAppRepository @Inject constructor(
    private val discoveryDataSource: AppDiscoveryDataSource,
    private val packageChangeMonitor: PackageChangeMonitor,
    private val metadataDao: AppMetadataDao,
    private val appLauncher: AppLauncher,
    @Named("selfPackageName") private val selfPackageName: String,
    @Dispatcher(LunmedDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : AppRepository {

    private val manualRefreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val discoveredAppsFlow = merge(
        packageChangeMonitor.events.onEach { event ->
            if (event is PackageChangeEvent.Removed && !event.isReplacing) {
                metadataDao.deleteByPackage(event.packageName)
            }
        },
        manualRefreshTrigger,
    ).mapLatest {
        val leanback = discoveryDataSource.queryLeanbackApps()
        val standard = discoveryDataSource.queryStandardApps()
        leanback to standard
    }.flowOn(ioDispatcher)

    override fun observeApps(includeHidden: Boolean): Flow<List<AppInfo>> =
        combine(
            discoveredAppsFlow,
            metadataDao.observeAll(),
        ) { (leanback, standard), metadataList ->
            val metadataMap = metadataList.associateBy { it.packageName }
            val merged = AppDiscoveryMerger.merge(
                leanbackEntries = leanback,
                standardEntries = standard,
                metadataByPackage = metadataMap,
                selfPackageName = selfPackageName,
            )
            if (includeHidden) merged else merged.filterNot { it.isHidden }
        }.flowOn(ioDispatcher)

    override suspend fun refresh() {
        manualRefreshTrigger.tryEmit(Unit)
    }

    override suspend fun launchApp(packageName: String): LaunchResult = withContext(ioDispatcher) {
        val result = appLauncher.launch(packageName)
        if (result is LaunchResult.Success) {
            val current = metadataDao.getByPackage(packageName) ?: AppMetadataEntity(packageName)
            metadataDao.upsert(
                current.copy(
                    lastLaunchedEpochMs = System.currentTimeMillis(),
                    launchCount = current.launchCount + 1,
                ),
            )
        }
        result
    }

    override suspend fun toggleFavorite(packageName: String) = withContext(ioDispatcher) {
        val current = metadataDao.getByPackage(packageName) ?: AppMetadataEntity(packageName)
        metadataDao.upsert(current.copy(isFavorite = !current.isFavorite))
    }

    override suspend fun setHidden(packageName: String, hidden: Boolean) = withContext(ioDispatcher) {
        val current = metadataDao.getByPackage(packageName) ?: AppMetadataEntity(packageName)
        metadataDao.upsert(current.copy(isHidden = hidden))
    }

    override suspend fun updateCustomOrder(orderedPackages: List<String>) = withContext(ioDispatcher) {
        orderedPackages.forEachIndexed { index, packageName ->
            val current = metadataDao.getByPackage(packageName) ?: AppMetadataEntity(packageName)
            metadataDao.upsert(current.copy(customOrder = index))
        }
    }

    override suspend fun setCategory(packageName: String, categoryId: String?) = withContext(ioDispatcher) {
        val current = metadataDao.getByPackage(packageName) ?: AppMetadataEntity(packageName)
        metadataDao.upsert(current.copy(categoryId = categoryId))
    }
}

