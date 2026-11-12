package com.lunmed.tv.core.ui.components

import androidx.compose.ui.graphics.Color
import com.lunmed.tv.core.model.AppInfo

/**
 * Intelligent Cinematic Ambient Aura Engine.
 * Dynamically computes luxurious harmonious light accents based on the focused
 * application's identity, category, or package signature.
 */
object AdaptiveAuraEngine {

    // Curated high-luxury jewel-tone palette for Android TV 4K/8K displays
    private val JewelTones = listOf(
        Color(0xFFE5C17B), // Champagne Gold
        Color(0xFF38BDF8), // Ice Cyan
        Color(0xFF818CF8), // Royal Indigo
        Color(0xFF34D399), // Emerald Jade
        Color(0xFFF472B6), // Radiant Rose
        Color(0xFFFBBF24), // Amber Sun
        Color(0xFF2DD4BF), // Deep Turquoise
        Color(0xFFA78BFA), // Electric Violet
    )

    fun auraForApp(app: AppInfo?, defaultColor: Color): Color {
        if (app == null) return defaultColor

        val pkg = app.packageName.lowercase()

        // 1. Signature entertainment applications
        return when {
            pkg.contains("netflix") -> Color(0xFFE50914) // Netflix Crimson
            pkg.contains("youtube") -> Color(0xFFFF2A2A) // YouTube Scarlet
            pkg.contains("spotify") -> Color(0xFF1DB954) // Spotify Emerald
            pkg.contains("disney") -> Color(0xFF1B4DFF) // Disney Royal Blue
            pkg.contains("prime") || pkg.contains("amazon") -> Color(0xFF00A8E1) // Prime Cyan
            pkg.contains("kodi") -> Color(0xFF17B2E7) // Kodi Ice
            pkg.contains("plex") -> Color(0xFFE5A00D) // Plex Amber
            pkg.contains("twitch") -> Color(0xFF9146FF) // Twitch Purple
            pkg.contains("apple") -> Color(0xFFE2E8F0) // Apple Platinum
            pkg.contains("hbo") || pkg.contains("max") -> Color(0xFF6B21A8) // Max Violet
            pkg.contains("vlc") -> Color(0xFFFF8800) // VLC Amber-Orange

            // 2. High-level category mapping
            else -> when (app.categoryId?.lowercase()) {
                "video", "movies", "tv" -> Color(0xFFE5C17B) // Gold
                "audio", "music" -> Color(0xFF10B981) // Emerald
                "game", "games" -> Color(0xFF8B5CF6) // Neon Violet
                "tools", "system" -> Color(0xFF38BDF8) // Ice Blue
                else -> {
                    // 3. Deterministic luxury jewel-tone hash
                    val index = (pkg.hashCode() and 0x7FFFFFFF) % JewelTones.size
                    JewelTones[index]
                }
            }
        }
    }
}

