package com.lunmed.tv.core.model

import java.util.Locale

/**
 * Authentic, zero-mock memory telemetry query from ActivityManager.
 */
data class MemoryDiagnostics(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val usageRatio: Float,
    val isLowMemory: Boolean,
    val thresholdBytes: Long,
) {
    val totalGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", totalBytes.toDouble() / (1024 * 1024 * 1024))
    val usedGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", usedBytes.toDouble() / (1024 * 1024 * 1024))
    val availableGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", availableBytes.toDouble() / (1024 * 1024 * 1024))
    val usagePercentageText: String
        get() = "${(usageRatio * 100).toInt()}%"
}

/**
 * Authentic, zero-mock internal/external filesystem storage query from StatFs.
 */
data class StorageDiagnostics(
    val totalBytes: Long,
    val availableBytes: Long,
    val usedBytes: Long,
    val usageRatio: Float,
) {
    val totalGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", totalBytes.toDouble() / (1024 * 1024 * 1024))
    val usedGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", usedBytes.toDouble() / (1024 * 1024 * 1024))
    val availableGbFormatted: String
        get() = String.format(Locale.US, "%.1f GB", availableBytes.toDouble() / (1024 * 1024 * 1024))
    val usagePercentageText: String
        get() = "${(usageRatio * 100).toInt()}%"
}

/**
 * Screen, refresh rate, and HDR capability telemetry.
 */
data class DisplayDiagnostics(
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val refreshRateHz: Float,
    val densityDpi: Int,
    val is4k: Boolean,
    val hdrSupportSummary: String,
)

/**
 * Hardware board, chipset, brand, and OS release diagnostics.
 */
data class HardwarePlatformDiagnostics(
    val brand: String,
    val manufacturer: String,
    val model: String,
    val product: String,
    val socOrBoard: String,
    val supportedAbis: String,
    val androidRelease: String,
    val sdkInt: Int,
    val securityPatch: String,
    val buildDisplay: String,
)

/**
 * Real-time network telemetry (Ethernet, Wi-Fi link speed, signal strength, IP address).
 */
data class NetworkDiagnostics(
    val transportName: String,
    val isConnected: Boolean,
    val ipAddress: String,
    val wifiSsid: String?,
    val wifiRssiDbm: Int?,
    val wifiLinkSpeedMbps: Int?,
)

/**
 * Consolidated live device telemetry report.
 */
data class FullDeviceReport(
    val memory: MemoryDiagnostics,
    val storage: StorageDiagnostics,
    val display: DisplayDiagnostics,
    val platform: HardwarePlatformDiagnostics,
    val network: NetworkDiagnostics,
    val timestampMs: Long = System.currentTimeMillis(),
)

