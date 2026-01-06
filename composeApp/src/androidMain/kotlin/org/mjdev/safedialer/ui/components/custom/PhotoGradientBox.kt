package org.mjdev.safedialer.ui.components.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import org.mjdev.safedialer.extensions.ColorExt.darker
import org.mjdev.safedialer.extensions.ColorExt.lighter
import org.mjdev.safedialer.helpers.Previews
import org.mjdev.safedialer.ui.theme.AppTheme

@Suppress("unused")
@Previews
@Composable
fun PhotoGradientBox(
    modifier: Modifier = Modifier,
    startColor: Color = Color.Transparent,
    endColor: Color = Color.Black,
    startAlpha: Float = 0.2f,
    endAlpha: Float = 0.9f,
    radialAlpha: Float = 0.3f,
    verticalAlpha: Float = 0.9f,
    startLightRatio: Float = 0.5f,
    endLightRatio: Float = 0.5f,
    shape: Shape = RectangleShape,
) = AppTheme {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val start = startColor.lighter(startLightRatio).copy(alpha = startAlpha)
        val end = endColor.darker(endLightRatio).copy(alpha = endAlpha)
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        Box(
            modifier = modifier.background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        end,
                    ),
                    center = Offset(-(width / 2).toFloat(), (height / 2).toFloat())
                ),
                shape = shape,
                alpha = radialAlpha
            )
        )
        Box(
            modifier = modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        start,
                        Color.Transparent,
                        Color.Transparent,
                        end,
                        end,
                    )
                ),
                shape = shape,
                alpha = verticalAlpha
            )
        )
    }
}
