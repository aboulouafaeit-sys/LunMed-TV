package com.lunmed.tv.core.design.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.lunmed.tv.core.model.AccentPreset

/**
 * LUNMED Signature UI Color Tokens.
 * Direction: Cinematic Luxury + Ultra Minimal + Futuristic Premium.
 */
@Immutable
data class LunmedColorTokens(
    // Core Deep Surfaces
    val obsidianCanvas: Color,
    val surfaceDeep: Color,
    val surfaceElevated: Color,
    val surfaceCard: Color,
    val surfaceCardFocused: Color,

    // Text & Content
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val textOnAccent: Color,

    // Accent & Ambient
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentGlow: Color,
    val ambientTop: Color,
    val ambientBottom: Color,

    // Borders & Hairlines
    val borderSubtle: Color,
    val borderFocused: Color,

    // Semantic Status
    val statusSuccess: Color,
    val statusWarning: Color,
    val statusError: Color,
    val statusInfo: Color,
)

object LunmedColors {
    val ObsidianCanvas = Color(0xFF07090E)
    val SurfaceDeep = Color(0xFF0C1018)
    val SurfaceElevated = Color(0xFF131926)
    val SurfaceCard = Color(0xFF171E2E)
    val SurfaceCardFocused = Color(0xFF1F293D)

    val TextPrimary = Color(0xFFF3F5F8)
    val TextSecondary = Color(0xFFA7B1C2)
    val TextMuted = Color(0xFF6B768A)
    val TextOnAccent = Color(0xFF0A0D14)

    val BorderSubtle = Color(0x22FFFFFF)

    val StatusSuccess = Color(0xFF4ADE80)
    val StatusWarning = Color(0xFFFBBF24)
    val StatusError = Color(0xFFF87171)
    val StatusInfo = Color(0xFF60A5FA)

    fun forAccent(preset: AccentPreset): LunmedColorTokens {
        val (primary, secondary, glow, ambientTop) = when (preset) {
            AccentPreset.CHAMPAGNE_GOLD -> Quad(
                Color(0xFFE5C17B),
                Color(0xFFC49A45),
                Color(0x55E5C17B),
                Color(0x18E5C17B),
            )
            AccentPreset.ICE_CYAN -> Quad(
                Color(0xFF7CE7FF),
                Color(0xFF38BDF8),
                Color(0x557CE7FF),
                Color(0x187CE7FF),
            )
            AccentPreset.PLATINUM_SILVER -> Quad(
                Color(0xFFE2E8F0),
                Color(0xFF94A3B8),
                Color(0x4DE2E8F0),
                Color(0x14E2E8F0),
            )
            AccentPreset.OBSIDIAN_AMBER -> Quad(
                Color(0xFFFFB26B),
                Color(0xFFF59E0B),
                Color(0x55FFB26B),
                Color(0x18FFB26B),
            )
        }

        return LunmedColorTokens(
            obsidianCanvas = ObsidianCanvas,
            surfaceDeep = SurfaceDeep,
            surfaceElevated = SurfaceElevated,
            surfaceCard = SurfaceCard,
            surfaceCardFocused = SurfaceCardFocused,
            textPrimary = TextPrimary,
            textSecondary = TextSecondary,
            textMuted = TextMuted,
            textOnAccent = TextOnAccent,
            accentPrimary = primary,
            accentSecondary = secondary,
            accentGlow = glow,
            ambientTop = ambientTop,
            ambientBottom = Color(0xFF05070B),
            borderSubtle = BorderSubtle,
            borderFocused = primary,
            statusSuccess = StatusSuccess,
            statusWarning = StatusWarning,
            statusError = StatusError,
            statusInfo = StatusInfo,
        )
    }

    private data class Quad(
        val primary: Color,
        val secondary: Color,
        val glow: Color,
        val ambientTop: Color,
    )
}

