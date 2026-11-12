package com.lunmed.tv.core.data.repository

import com.lunmed.tv.core.data.apps.AppDiscoveryDataSource
import com.lunmed.tv.core.data.apps.AppLauncher
import com.lunmed.tv.core.data.apps.DiscoveredAppEntry
import com.lunmed.tv.core.data.apps.PackageChangeEvent
import com.lunmed.tv.core.data.apps.PackageChangeMonitor
import com.lunmed.tv.core.database.dao.AppMetadataDao
import com.lunmed.tv.core.database.entity.AppMetadataEntity
import com.lunmed.tv.core.model.LaunchResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAppRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `toggleFavorite and setHidden update observed apps`() = runTest(testDispatcher) {
        val dao = FakeAppMetadataDao()
        val repo = createRepo(
            apps = listOf(
                sampleEntry("com.media.one", "Media One"),
                sampleEntry("com.media.two", "Media Two"),
            ),
            dao = dao,
        )

        val initial = repo.observeApps().first()
        assertEquals(2, initial.size)

        repo.toggleFavorite("com.media.one")
        val afterFav = repo.observeApps().first()
        assertTrue(afterFav.first { it.packageName == "com.media.one" }.isFavorite)

        repo.setHidden("com.media.two", true)
        val visibleOnly = repo.observeApps(includeHidden = false).first()
        val withHidden = repo.observeApps(includeHidden = true).first()
        assertEquals(1, visibleOnly.size)
        assertEquals(2, withHidden.size)
        assertFalse(visibleOnly.any { it.packageName == "com.media.two" })
    }

    @Test
    fun `launchApp increments count on success and preserves count on failure`() = runTest(testDispatcher) {
        val dao = FakeAppMetadataDao()
        val launcher = FakeAppLauncher(
            results = mapOf(
                "com.ok" to LaunchResult.Success,
                "com.broken" to LaunchResult.PackageUnavailable("com.broken"),
            ),
        )
        val repo = createRepo(
            apps = listOf(sampleEntry("com.ok", "OK"), sampleEntry("com.broken", "Broken")),
            dao = dao,
            launcher = launcher,
        )

        assertEquals(LaunchResult.Success, repo.launchApp("com.ok"))
        assertEquals(1, dao.getByPackage("com.ok")?.launchCount)

        val failResult = repo.launchApp("com.broken")
        assertTrue(failResult is LaunchResult.PackageUnavailable)
        assertNull(dao.getByPackage("com.broken"))
    }

    @Test
    fun `package removal cleans metadata only when not replacing`() = runTest(testDispatcher) {
        val dao = FakeAppMetadataDao()
        val monitor = FakePackageChangeMonitor()
        val repo = createRepo(
            apps = listOf(sampleEntry("com.app", "App")),
            dao = dao,
            monitor = monitor,
        )

        repo.toggleFavorite("com.app")
        assertTrue(dao.getByPackage("com.app")?.isFavorite == true)

        // Start collecting so monitor events are processed
        repo.observeApps().first()

        // Replacing (app update) keeps favorite metadata
        monitor.emit(PackageChangeEvent.Removed("com.app", isReplacing = true))
        repo.observeApps().first()
        assertTrue(dao.getByPackage("com.app")?.isFavorite == true)

        // Full uninstall deletes metadata
        monitor.emit(PackageChangeEvent.Removed("com.app", isReplacing = false))
        repo.observeApps().first()
        assertNull(dao.getByPackage("com.app"))
    }

    private fun createRepo(
        apps: List<DiscoveredAppEntry>,
        dao: FakeAppMetadataDao = FakeAppMetadataDao(),
        monitor: FakePackageChangeMonitor = FakePackageChangeMonitor(),
        launcher: AppLauncher = FakeAppLauncher(emptyMap()),
    ) = DefaultAppRepository(
        discoveryDataSource = object : AppDiscoveryDataSource {
            override suspend fun queryLeanbackApps(): List<DiscoveredAppEntry> = apps
            override suspend fun queryStandardApps(): List<DiscoveredAppEntry> = emptyList()
        },
        packageChangeMonitor = monitor,
        metadataDao = dao,
        appLauncher = launcher,
        selfPackageName = "com.lunmed.tv",
        ioDispatcher = testDispatcher,
    )

    private fun sampleEntry(pkg: String, label: String) = DiscoveredAppEntry(
        packageName = pkg,
        activityName = "$pkg.Main",
        label = label,
        isTvApp = true,
        isSystemApp = false,
        inferredCategoryId = "media",
    )

    private class FakeAppMetadataDao : AppMetadataDao {
        private val state = MutableStateFlow<Map<String, AppMetadataEntity>>(emptyMap())

        override fun observeAll(): Flow<List<AppMetadataEntity>> =
            state.map { it.values.sortedBy { e -> e.customOrder } }

        override suspend fun getByPackage(packageName: String): AppMetadataEntity? =
            state.value[packageName]

        override suspend fun upsert(entity: AppMetadataEntity) {
            state.value = state.value + (entity.packageName to entity)
        }

        override suspend fun deleteByPackage(packageName: String) {
            state.value = state.value - packageName
        }
    }

    private class FakePackageChangeMonitor : PackageChangeMonitor {
        private val flow = MutableSharedFlow<PackageChangeEvent>(replay = 1)

        init {
            flow.tryEmit(PackageChangeEvent.Initial)
        }

        override val events: Flow<PackageChangeEvent> = flow

        suspend fun emit(event: PackageChangeEvent) {
            flow.emit(event)
        }
    }

    private class FakeAppLauncher(
        private val results: Map<String, LaunchResult>,
    ) : AppLauncher {
        override fun launch(packageName: String): LaunchResult =
            results[packageName] ?: LaunchResult.PackageUnavailable(packageName)
    }
}

