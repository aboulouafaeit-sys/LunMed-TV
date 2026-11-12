package com.lunmed.tv.core.common.di

import com.lunmed.tv.core.common.capability.AndroidDeviceCapabilityDetector
import com.lunmed.tv.core.common.capability.DeviceCapabilityDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindDeviceCapabilityDetector(
        impl: AndroidDeviceCapabilityDetector,
    ): DeviceCapabilityDetector
}

