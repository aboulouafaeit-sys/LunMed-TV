package com.lunmed.tv.core.database.di

import android.content.Context
import androidx.room.Room
import com.lunmed.tv.core.database.LunmedDatabase
import com.lunmed.tv.core.database.dao.AppCategoryDao
import com.lunmed.tv.core.database.dao.AppMetadataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLunmedDatabase(
        @ApplicationContext context: Context,
    ): LunmedDatabase = Room.databaseBuilder(
        context,
        LunmedDatabase::class.java,
        "lunmed_tv.db",
    ).fallbackToDestructiveMigration(dropAllTables = false)
        .build()

    @Provides
    fun provideAppMetadataDao(db: LunmedDatabase): AppMetadataDao = db.appMetadataDao()

    @Provides
    fun provideAppCategoryDao(db: LunmedDatabase): AppCategoryDao = db.appCategoryDao()
}

