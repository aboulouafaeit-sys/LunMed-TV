package com.lunmed.tv.core.model

/**
 * Typed result of attempting to launch an installed application.
 */
sealed interface LaunchResult {
    data object Success : LaunchResult
    data class PackageUnavailable(val packageName: String) : LaunchResult
    data class PermissionDenied(val packageName: String) : LaunchResult
    data class Failed(val packageName: String, val reason: String) : LaunchResult
}

/**
 * Current default-home registration state of LUNMED TV.
 */
data class DefaultLauncherState(
    val isDefaultLauncher: Boolean,
    val currentDefaultPackage: String?,
    val canRequestRole: Boolean,
    /** True when the Accessibility Home Watchdog service is enabled and bound by the system. */
    val isWatchdogEnabled: Boolean = false,
    /** True when our DeviceAdminReceiver is an active device administrator. */
    val isDeviceAdminActive: Boolean = false,
    /** True when the app is Device Owner (allows immutable persistent home preference). */
    val isDeviceOwner: Boolean = false,
)

