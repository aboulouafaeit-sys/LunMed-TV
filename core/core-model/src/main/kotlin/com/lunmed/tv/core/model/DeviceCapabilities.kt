package com.lunmed.tv.core.model

/**
 * Runtime hardware and OS capability snapshot used to adapt LUNMED TV
 * behavior across diverse Android TV, Google TV, and AOSP TV Box hardware.
 */
data class DeviceCapabilities(
    val sdkInt: Int,
    val isLeanbackSupported: Boolean,
    val isTelevisionUiMode: Boolean,
    val hasTouchscreen: Boolean,
    val hasDpad: Boolean,
    val isLowRamDevice: Boolean,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val densityDpi: Int,
) {
    val is4kDisplay: Boolean
        get() = screenWidthPx >= 3840 || screenHeightPx >= 2160

    val recommendedAnimationIntensity: AnimationIntensity
        get() = if (isLowRamDevice) AnimationIntensity.REDUCED else AnimationIntensity.FULL
}

