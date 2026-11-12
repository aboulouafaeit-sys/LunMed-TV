package com.lunmed.tv.core.data.di

import com.lunmed.tv.core.data.repository.DefaultPreferencesRepository
import com.lunmed.tv.core.data.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: DefaultPreferencesRepository,
    ): PreferencesRepository
}

