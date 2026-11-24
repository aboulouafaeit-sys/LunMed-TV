package com.lunmed.tv.core.design.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 10-foot television typography scale optimized for legibility at 2.5m-4m viewing distance.
 *
 * Sizes were re-tuned for real living-room distance: nothing renders below 12sp and the
 * body/label tiers were lifted one step (13->14sp, 11->12sp, 15->16sp) so metadata stays
 * readable on a 1080p panel without leaning in. Individual screens may still override a
 * tier, but they should never drop below [MIN_LEGIBLE_SP].
 */
@Immutable
data class LunmedTypographyTokens(
    val displayHero: TextStyle,
    val headlineSection: TextStyle,
    val titleLarge: TextStyle,
    val titleCard: TextStyle,
    val bodyPrimary: TextStyle,
    val bodySecondary: TextStyle,
    val labelAction: TextStyle,
    val labelMeta: TextStyle,
)

object LunmedTypography {
    /** Hard floor for any text rendered on a TV at viewing distance. */
    const val MIN_LEGIBLE_SP = 12

    val Default = LunmedTypographyTokens(
        displayHero = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Light,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.5).sp,
        ),
        headlineSection = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.2.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.1.sp,
        ),
        titleCard = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.15.sp,
        ),
        bodyPrimary = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 23.sp,
            letterSpacing = 0.15.sp,
        ),
        bodySecondary = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.2.sp,
        ),
        labelAction = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.4.sp,
        ),
        labelMeta = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            letterSpacing = 0.5.sp,
        ),
    )
}
