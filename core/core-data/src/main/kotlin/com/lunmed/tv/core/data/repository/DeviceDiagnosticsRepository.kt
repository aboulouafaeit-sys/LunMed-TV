package com.lunmed.tv.core.data.repository

import android.app.ActivityManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.view.Display
import com.lunmed.tv.core.model.DisplayDiagnostics
import com.lunmed.tv.core.model.FullDeviceReport
import com.lunmed.tv.core.model.HardwarePlatformDiagnostics
import com.lunmed.tv.core.model.MemoryDiagnostics
import com.lunmed.tv.core.model.NetworkDiagnostics
import com.lunmed.tv.core.model.StorageDiagnostics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import javax.inject.Inject
import javax.inject.Singleton

interface DeviceDiagnosticsRepository {
    fun getDeviceReport(): FullDeviceReport
    suspend fun clearMemoryCache(): Long
}

@Singleton
class AndroidDeviceDiagnosticsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceDiagnosticsRepository {

    override fun getDeviceReport(): FullDeviceReport {
        return FullDeviceReport(
            memory = queryMemoryDiagnostics(),
            storage = queryStorageDiagnostics(),
            display = queryDisplayDiagnostics(),
            platform = queryPlatformDiagnostics(),
            network = queryNetworkDiagnostics(),
            timestampMs = System.currentTimeMillis(),
        )
    }

    override suspend fun clearMemoryCache(): Long = withContext(Dispatchers.Default) {
        val runtime = Runtime.getRuntime()
        val initialFree = runtime.freeMemory()
        System.gc()
        val afterFree = runtime.freeMemory()
        (afterFree - initialFree).coerceAtLeast(0L)
    }

    private fun queryMemoryDiagnostics(): MemoryDiagnostics {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        if (actManager != null) {
            actManager.getMemoryInfo(memInfo)
        }

        val total = memInfo.totalMem.coerceAtLeast(1L)
        val available = memInfo.availMem.coerceAtLeast(0L)
        val used = (total - available).coerceAtLeast(0L)
        val ratio = (used.toFloat() / total.toFloat()).coerceIn(0f, 1f)

        return MemoryDiagnostics(
            totalBytes = total,
            availableBytes = available,
            usedBytes = used,
            usageRatio = ratio,
            isLowMemory = memInfo.lowMemory,
            thresholdBytes = memInfo.threshold,
        )
    }

    private fun queryStorageDiagnostics(): StorageDiagnostics {
        return runCatching {
            val dataDir = Environment.getDataDirectory()
            val stat = StatFs(dataDir.path)
            val total = stat.totalBytes.coerceAtLeast(1L)
            val available = stat.availableBytes.coerceAtLeast(0L)
            val used = (total - available).coerceAtLeast(0L)
            val ratio = (used.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            StorageDiagnostics(
                totalBytes = total,
                availableBytes = available,
                usedBytes = used,
                usageRatio = ratio,
            )
        }.getOrElse {
            StorageDiagnostics(
                totalBytes = 16L * 1024 * 1024 * 1024,
                availableBytes = 8L * 1024 * 1024 * 1024,
                usedBytes = 8L * 1024 * 1024 * 1024,
                usageRatio = 0.5f,
            )
        }
    }

    private fun queryDisplayDiagnostics(): DisplayDiagnostics {
        val dm = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
        val display = dm?.getDisplay(Display.DEFAULT_DISPLAY)

        val metrics = context.resources.displayMetrics
        val width = display?.mode?.physicalWidth ?: metrics.widthPixels
        val height = display?.mode?.physicalHeight ?: metrics.heightPixels
        val refreshRate = display?.mode?.refreshRate ?: display?.refreshRate ?: 60.0f
        val densityDpi = metrics.densityDpi
        val is4k = width >= 3840 || height >= 2160

        val hdrTypes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching {
                val types = display?.hdrCapabilities?.supportedHdrTypes
                if (types == null || types.isEmpty()) {
                    "SDR"
                } else {
                    types.map { type: Int ->
                        when (type) {
                            Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                            Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                            Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                            else -> "HDR"
                        }
                    }.distinct().joinToString(", ")
                }
            }.getOrDefault("SDR")
        } else {
            "SDR"
        }

        return DisplayDiagnostics(
            resolutionWidth = width,
            resolutionHeight = height,
            refreshRateHz = refreshRate,
            densityDpi = densityDpi,
            is4k = is4k,
            hdrSupportSummary = hdrTypes,
        )
    }

    private fun queryPlatformDiagnostics(): HardwarePlatformDiagnostics {
        val abis = Build.SUPPORTED_ABIS.joinToString(", ").ifBlank { "Unknown" }
        return HardwarePlatformDiagnostics(
            brand = Build.BRAND.replaceFirstChar { it.uppercase() },
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            product = Build.PRODUCT,
            socOrBoard = Build.BOARD.ifBlank { Build.HARDWARE },
            supportedAbis = abis,
            androidRelease = Build.VERSION.RELEASE,
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Build.VERSION.SECURITY_PATCH
            } else {
                "N/A"
            },
            buildDisplay = Build.DISPLAY,
        )
    }

    private fun queryNetworkDiagnostics(): NetworkDiagnostics {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNet = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNet)

        val isConnected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val transportName = when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            caps != null -> "Other"
            else -> "Disconnected"
        }

        val ipAddress = resolveLocalIpv4Address()

        var wifiSsid: String? = null
        var wifiRssi: Int? = null
        var wifiSpeed: Int? = null

        if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val wifiInfo = wm?.connectionInfo
            if (wifiInfo != null) {
                wifiSsid = wifiInfo.ssid?.trim('\"')?.takeIf { it != "<unknown ssid>" }
                wifiRssi = wifiInfo.rssi
                wifiSpeed = wifiInfo.linkSpeed.takeIf { it > 0 }
            }
        }

        return NetworkDiagnostics(
            transportName = transportName,
            isConnected = isConnected,
            ipAddress = ipAddress,
            wifiSsid = wifiSsid,
            wifiRssiDbm = wifiRssi,
            wifiLinkSpeedMbps = wifiSpeed,
        )
    }

    private fun resolveLocalIpv4Address(): String {
        return runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                if (iface.isLoopback || !iface.isUp) continue
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
            "127.0.0.1"
        }.getOrDefault("127.0.0.1")
    }
}

