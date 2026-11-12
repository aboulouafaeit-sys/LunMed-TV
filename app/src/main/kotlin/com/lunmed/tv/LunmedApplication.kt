package com.lunmed.tv

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.lunmed.tv.core.ui.artwork.AppArtworkCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LunmedApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val isLowRam = activityManager?.isLowRamDevice ?: false
        AppArtworkCache.configureForLowRam(isLowRam)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        AppArtworkCache.trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        AppArtworkCache.clear()
    }
}


