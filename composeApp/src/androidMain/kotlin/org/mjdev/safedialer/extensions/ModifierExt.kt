package org.mjdev.safedialer.extensions

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Suppress("unused")
object ModifierExt {

    fun Modifier.applyIf(
        condition: Boolean,
        other: Modifier.() -> Modifier
    ): Modifier = if (condition) this.then(other()) else this

    @Composable
    fun Modifier.dashedBorder(
        width: Dp= 1.dp,
        radius: Dp= 0.dp,
        color: Color= Color.White
    ) = drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                strokeWidth = width.toPx()
                this.color = color
                style = PaintingStyle.Stroke
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            }
            canvas.drawRoundRect(
                width.toPx(),
                width.toPx(),
                size.width - width.toPx(),
                size.height - width.toPx(),
                radius.toPx(),
                radius.toPx(),
                paint
            )
        }
    }

    fun Modifier.drawOutlineHaloShadowBlur(
        color: Color = Color.Black,
        blurRadius: Dp = 4.dp,
        haloBorderWidth: Dp = 1.dp,
        innerCircleContentSize: Dp = 1.dp,
    ) = if (haloBorderWidth > 0.dp) {
        graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }.drawBehind {
            val innerContentSizePx = innerCircleContentSize.toPx()
            val haloBorderWidthPx = haloBorderWidth.toPx()
            //todo it is better to create Paint outside, remember and reuse this object
            val paint = Paint().apply {
                this.color = color
                this.style = PaintingStyle.Stroke
                this.strokeWidth = haloBorderWidthPx
            }
            if (blurRadius.toPx() > 0) {
                paint
                    .asFrameworkPaint()
                    .apply {
                        maskFilter = BlurMaskFilter(
                            blurRadius.toPx(),
                            BlurMaskFilter.Blur.NORMAL
                        )
                    }
            } else {
                paint.asFrameworkPaint().maskFilter = null
            }
            val shadowSize = Size(
                width = innerContentSizePx + haloBorderWidthPx,
                height = innerContentSizePx + haloBorderWidthPx
            )
            val shadowOutline = CircleShape
                .createOutline(shadowSize, layoutDirection, this)
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.translate(
                    (size.width - innerContentSizePx) / 2f - haloBorderWidthPx / 2,
                    (size.height - innerContentSizePx) / 2f - haloBorderWidthPx / 2
                )
                canvas.drawOutline(shadowOutline, paint)
                canvas.restore()
                // Clear the center area
                drawCircle(
                    radius = innerContentSizePx / 2,
                    center = center,
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear
                )
            }
        }
    } else {
        this
    }

}
