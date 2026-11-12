package com.lunmed.tv.core.data.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.model.NetworkTransport
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkStatusMonitor {
    val networkStatus: Flow<NetworkStatus>
}

@Singleton
class AndroidNetworkStatusMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkStatusMonitor {

    override val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm == null) {
            trySend(NetworkStatus(isConnected = false, transport = NetworkTransport.OFFLINE))
            close()
            return@callbackFlow
        }

        fun currentStatus(): NetworkStatus {
            val activeNetwork = cm.activeNetwork
                ?: return NetworkStatus(isConnected = false, transport = NetworkTransport.OFFLINE)
            val caps = cm.getNetworkCapabilities(activeNetwork)
                ?: return NetworkStatus(isConnected = false, transport = NetworkTransport.OFFLINE)

            val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val transport = when {
                !hasInternet -> NetworkTransport.OFFLINE
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkTransport.ETHERNET
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkTransport.WIFI
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkTransport.CELLULAR
                else -> NetworkTransport.OTHER
            }
            return NetworkStatus(isConnected = hasInternet, transport = transport)
        }

        trySend(currentStatus())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(currentStatus())
            }

            override fun onLost(network: Network) {
                trySend(currentStatus())
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(currentStatus())
            }
        }

        val registered = runCatching {
            cm.registerDefaultNetworkCallback(callback)
        }.isSuccess

        awaitClose {
            if (registered) {
                runCatching { cm.unregisterNetworkCallback(callback) }
            }
        }
    }.distinctUntilChanged()
}

