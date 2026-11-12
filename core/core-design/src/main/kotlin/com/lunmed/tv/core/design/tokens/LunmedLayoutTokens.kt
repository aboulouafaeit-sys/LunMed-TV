package com.lunmed.tv.core.design.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lunmed.tv.core.model.InterfaceDensity

@Immutable
data class LunmedSpacingTokens(
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val overscanHorizontal: Dp,
    val overscanVertical: Dp,
    val sectionGap: Dp,
    val cardGap: Dp,
)

object LunmedSpacing {
    fun forDensity(density: InterfaceDensity): LunmedSpacingTokens = when (density) {
        InterfaceDensity.COMFORTABLE -> LunmedSpacingTokens(
            xxs = 4.dp,
            xs = 8.dp,
            sm = 12.dp,
            md = 16.dp,
            lg = 24.dp,
            xl = 32.dp,
            xxl = 48.dp,
            overscanHorizontal = 48.dp,
            overscanVertical = 28.dp,
            sectionGap = 28.dp,
            cardGap = 16.dp,
        )
        InterfaceDensity.COMPACT -> LunmedSpacingTokens(
            xxs = 2.dp,
            xs = 6.dp,
            sm = 10.dp,
            md = 12.dp,
            lg = 18.dp,
            xl = 24.dp,
            xxl = 36.dp,
            overscanHorizontal = 40.dp,
            overscanVertical = 22.dp,
            sectionGap = 20.dp,
            cardGap = 12.dp,
        )
    }
}

@Immutable
data class LunmedShapeTokens(
    val cardSmall: Shape,
    val cardMedium: Shape,
    val cardLarge: Shape,
    val panel: Shape,
    val pill: Shape,
)

object LunmedShapes {
    val Default = LunmedShapeTokens(
        cardSmall = RoundedCornerShape(10.dp),
        cardMedium = RoundedCornerShape(14.dp),
        cardLarge = RoundedCornerShape(18.dp),
        panel = RoundedCornerShape(20.dp),
        pill = RoundedCornerShape(50),
    )
}

@Immutable
data class LunmedElevationTokens(
    val none: Dp,
    val subtle: Dp,
    val card: Dp,
    val focused: Dp,
    val overlay: Dp,
)

object LunmedElevation {
    val Default = LunmedElevationTokens(
        none = 0.dp,
        subtle = 2.dp,
        card = 4.dp,
        focused = 10.dp,
        overlay = 16.dp,
    )
}

