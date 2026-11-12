package com.lunmed.tv.feature.settings.model

enum class SettingsSection(val title: String, val subtitle: String) {
    APPEARANCE(
        title = "Appearance & Theme",
        subtitle = "Accents, density, and card sizing",
    ),
    ANIMATIONS(
        title = "Motion & Performance",
        subtitle = "Animation intensity and TV hardware optimization",
    ),
    HOME_SCREEN(
        title = "Home Customization",
        subtitle = "Clock, status indicators, and recent apps row",
    ),
    DEFAULT_LAUNCHER(
        title = "Default Launcher & System",
        subtitle = "Home role status and Android system settings",
    ),
    ABOUT(
        title = "About & Device Info",
        subtitle = "Software version, hardware capabilities, and diagnostics",
    ),
}

