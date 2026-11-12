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
)
