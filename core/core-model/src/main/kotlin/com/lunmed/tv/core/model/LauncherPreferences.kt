package com.lunmed.tv.core.model

enum class AccentPreset(val key: String) {
    CHAMPAGNE_GOLD("champagne_gold"),
    ICE_CYAN("ice_cyan"),
    PLATINUM_SILVER("platinum_silver"),
    OBSIDIAN_AMBER("obsidian_amber"),
}

enum class CardSizePreset(val key: String) {
    COMPACT("compact"),
    STANDARD("standard"),
    CINEMA("cinema"),
}

enum class InterfaceDensity(val key: String) {
    COMFORTABLE("comfortable"),
    COMPACT("compact"),
}

enum class AnimationIntensity(val key: String) {
    FULL("full"),
    REDUCED("reduced"),
    MINIMAL("minimal"),
}

/**
 * Persistent user customization preferences for LUNMED TV.
 */
data class LauncherPreferences(
    val accentPreset: AccentPreset = AccentPreset.CHAMPAGNE_GOLD,
    val cardSize: CardSizePreset = CardSizePreset.STANDARD,
    val density: InterfaceDensity = InterfaceDensity.COMFORTABLE,
    val animationIntensity: AnimationIntensity = AnimationIntensity.FULL,
    val showClock: Boolean = true,
    val showDeviceIndicators: Boolean = true,
    val showRecentApps: Boolean = true,
    val languageTag: String = "system",
    val selectedWallpaperId: String = "lunmed_obsidian_01",
)

