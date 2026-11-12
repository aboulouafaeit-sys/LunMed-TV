package com.lunmed.tv.core.data.di

import android.content.Context
import com.lunmed.tv.core.data.apps.AndroidAppLauncher
import com.lunmed.tv.core.data.apps.AndroidDefaultLauncherHelper
import com.lunmed.tv.core.data.apps.AndroidPackageChangeMonitor
import com.lunmed.tv.core.data.apps.AppDiscoveryDataSource
import com.lunmed.tv.core.data.apps.AppLauncher
import com.lunmed.tv.core.data.apps.DefaultLauncherHelper
import com.lunmed.tv.core.data.apps.PackageChangeMonitor
import com.lunmed.tv.core.data.apps.PackageManagerAppDiscoveryDataSource
import com.lunmed.tv.core.data.repository.AppRepository
import com.lunmed.tv.core.data.repository.DefaultAppRepository
import com.lunmed.tv.core.data.repository.DefaultPreferencesRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import com.lunmed.tv.core.data.system.AndroidNetworkStatusMonitor
import com.lunmed.tv.core.data.system.NetworkStatusMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: DefaultPreferencesRepository,
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAppDiscoveryDataSource(
        impl: PackageManagerAppDiscoveryDataSource,
    ): AppDiscoveryDataSource

    @Binds
    @Singleton
    abstract fun bindPackageChangeMonitor(
        impl: AndroidPackageChangeMonitor,
    ): PackageChangeMonitor

    @Binds
    @Singleton
    abstract fun bindAppLauncher(
        impl: AndroidAppLauncher,
    ): AppLauncher

    @Binds
    @Singleton
    abstract fun bindDefaultLauncherHelper(
        impl: AndroidDefaultLauncherHelper,
    ): DefaultLauncherHelper

    @Binds
    @Singleton
    abstract fun bindAppRepository(
        impl: DefaultAppRepository,
    ): AppRepository

    @Binds
    @Singleton
    abstract fun bindNetworkStatusMonitor(
        impl: AndroidNetworkStatusMonitor,
    ): NetworkStatusMonitor

    @Binds
    @Singleton
    abstract fun bindDeviceDiagnosticsRepository(
        impl: com.lunmed.tv.core.data.repository.AndroidDeviceDiagnosticsRepository,
    ): com.lunmed.tv.core.data.repository.DeviceDiagnosticsRepository

    @Binds
    @Singleton
    abstract fun bindWallpaperRepository(
        impl: com.lunmed.tv.core.data.repository.DefaultWallpaperRepository,
    ): com.lunmed.tv.core.data.repository.WallpaperRepository

    @Binds
    @Singleton
    abstract fun bindRootLauncherHelper(
        impl: com.lunmed.tv.core.data.root.AndroidRootLauncherHelper,
    ): com.lunmed.tv.core.data.root.RootLauncherHelper

    companion object {
        @Provides
        @Named("selfPackageName")
        fun provideSelfPackageName(
            @ApplicationContext context: Context,
        ): String = context.packageName
    }
}
