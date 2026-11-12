package com.lunmed.tv.core.data.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PackageChangeEvent {
    data object Initial : PackageChangeEvent
    data class Added(val packageName: String) : PackageChangeEvent
    data class Removed(val packageName: String, val isReplacing: Boolean) : PackageChangeEvent
    data class Changed(val packageName: String) : PackageChangeEvent
}

interface PackageChangeMonitor {
    val events: Flow<PackageChangeEvent>
}

@Singleton
class AndroidPackageChangeMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : PackageChangeMonitor {

    override val events: Flow<PackageChangeEvent> = callbackFlow {
        trySend(PackageChangeEvent.Initial)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val pkg = intent?.data?.schemeSpecificPart ?: return
                val replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                when (intent.action) {
                    Intent.ACTION_PACKAGE_ADDED -> trySend(PackageChangeEvent.Added(pkg))
                    Intent.ACTION_PACKAGE_REMOVED -> trySend(PackageChangeEvent.Removed(pkg, replacing))
                    Intent.ACTION_PACKAGE_REPLACED,
                    Intent.ACTION_PACKAGE_CHANGED -> trySend(PackageChangeEvent.Changed(pkg))
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        awaitClose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}

