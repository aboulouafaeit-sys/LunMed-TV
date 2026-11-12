package com.lunmed.tv.core.model

enum class NetworkTransport {
    ETHERNET,
    WIFI,
    CELLULAR,
    OTHER,
    OFFLINE,
}

data class NetworkStatus(
    val isConnected: Boolean,
    val transport: NetworkTransport,
)

