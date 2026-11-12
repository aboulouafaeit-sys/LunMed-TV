package com.lunmed.tv.core.model

enum class ScenicType {
    NORDIC_AURORA,
    COSMIC_NEBULA,
    SAHARA_SUNSET,
    ALPINE_MIST,
    TOKYO_CYBER,
    OBSIDIAN_SILK,
}

/**
 * Cinematic wallpaper definition for LUNMED TV luxury backdrop.
 */
data class LunmedWallpaper(
    val id: String,
    val title: String,
    val description: String,
    val primaryColorArgb: Long,
    val secondaryColorArgb: Long,
    val ambientGlowArgb: Long,
    val scenicType: ScenicType = ScenicType.OBSIDIAN_SILK,
    val isCustom: Boolean = false,
    val customUri: String? = null,
)

object LunmedWallpapers {
    val BUILT_IN: List<LunmedWallpaper> = listOf(
        LunmedWallpaper(
            id = "lunmed_aurora_nordic",
            title = "Nordic Aurora Borealis",
            description = "Breathtaking dancing emerald & violet aurora ribbons above midnight polar mountain ridges.",
            primaryColorArgb = 0xFF040A14,
            secondaryColorArgb = 0xFF0A1B2A,
            ambientGlowArgb = 0xFF00FF87,
            scenicType = ScenicType.NORDIC_AURORA,
        ),
        LunmedWallpaper(
            id = "lunmed_cosmic_nebula",
            title = "Orion Cosmic Nebula",
            description = "Deep celestial stellar nursery with sparkling star clusters and interstellar magenta dust.",
            primaryColorArgb = 0xFF070312,
            secondaryColorArgb = 0xFF16092A,
            ambientGlowArgb = 0xFFFF007F,
            scenicType = ScenicType.COSMIC_NEBULA,
        ),
        LunmedWallpaper(
            id = "lunmed_sunset_sahara",
            title = "Sahara Dune Twilight",
            description = "Warm crimson and amber twilight gradient sweeping across majestic undulating sand dunes.",
            primaryColorArgb = 0xFF140703,
            secondaryColorArgb = 0xFF2A0F08,
            ambientGlowArgb = 0xFFFF9100,
            scenicType = ScenicType.SAHARA_SUNSET,
        ),
        LunmedWallpaper(
            id = "lunmed_alpine_mist",
            title = "Alpine Mountain Mist",
            description = "Crisp atmospheric cobalt blue mist flowing through monumental snow-capped alpine peaks.",
            primaryColorArgb = 0xFF050B14,
            secondaryColorArgb = 0xFF0F1E30,
            ambientGlowArgb = 0xFF80D8FF,
            scenicType = ScenicType.ALPINE_MIST,
        ),
        LunmedWallpaper(
            id = "lunmed_tokyo_cyber",
            title = "Neo Tokyo Twilight",
            description = "Futuristic nocturnal cityscape horizon glowing with vibrant cyan and neon violet reflections.",
            primaryColorArgb = 0xFF080412,
            secondaryColorArgb = 0xFF140A28,
            ambientGlowArgb = 0xFF00E5FF,
            scenicType = ScenicType.TOKYO_CYBER,
        ),
        LunmedWallpaper(
            id = "lunmed_obsidian_silk",
            title = "Signature Obsidian Silk",
            description = "Ultra-luxurious velvet black obsidian canvas infused with fluid 24K champagne gold luster.",
            primaryColorArgb = 0xFF050505,
            secondaryColorArgb = 0xFF0E0E10,
            ambientGlowArgb = 0xFFD4AF37,
            scenicType = ScenicType.OBSIDIAN_SILK,
        ),
    )

    fun getById(id: String?): LunmedWallpaper =
        BUILT_IN.find { it.id == id } ?: BUILT_IN.first()
}

