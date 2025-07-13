package org.mjdev.safedialer.extensions

import android.content.Context
import android.graphics.BlurMaskFilter
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache

object ComposeExt1 {

    val ScrollableState.canScroll
        get() = canScrollForward || canScrollBackward

    @Composable
    fun rememberImageLoader(
        context: Context = LocalContext.current
    ) = remember {
        ImageLoader.Builder(context)
//            .okHttpClient { OkHttpClient.Builder().build() }
            .crossfade(true)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }
            .components {
                // Add any custom components here if needed
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.5)
                    .build()
            }
            .build()
    }

    /**
     * An extension modifier for creating a circular shadow with a blurred halo effect
     * around a composable. This function draws an outline shadow using a `BlurMaskFilter`
     * to simulate a blur effect with a configurable halo border. The inner circle remains
     * transparent to create an outlined appearance.
     *
     * **Note:** The size of the composable should be large enough to fit the entire
     * halo effect. The `innerCircleContentSize` plus the blurred halo border should be
     * smaller than the composable's size to ensure the halo is not clipped. This is
     * because the `graphicsLayer` with `CompositingStrategy.Offscreen` prevents drawing
     * outside the composable bounds, so the halo will be clipped if it exceeds the
     * composable's size.
     *
     * The `graphicsLayer` with the `CompositingStrategy.Offscreen` is used to allow
     * clipping of the inner circle with a `BlendMode.Clear` to make it transparent.
     * This solution is especially useful when creating circular transparent outlined
     * buttons on top of a complex background, such as an image or a gradient, rather
     * than a simple solid color background.
     *
     * **BlurMaskFilter Issues on Older API Levels:** Note that `BlurMaskFilter` may
     * not work consistently on older Android API levels, particularly on API 26 (Android
     * 8.0) and lower. On these devices, the blur effect may not render as intended or
     * could be less visually appealing, resulting in inconsistent halo effects. This
     * limitation is due to platform-specific differences in how `BlurMaskFilter` is
     * implemented across various Android versions.
     *
     * **Alternative Solution:** If you encounter issues with `BlurMaskFilter` on
     * older devices (API 26 and below), you can use a radial gradient to create the
     * halo effect instead of relying on `BlurMaskFilter`. This approach uses a gradient
     * with varying alpha values to simulate a blurred edge, providing a similar visual
     * effect that is more consistent across different Android versions. See
     * [drawOutlineCircularShadowGradient] for an example of how to implement the
     * gradient-based halo effect.
     *
     * For cases where drawing outside the composable bounds is required, consider
     * avoiding the use of `CompositingStrategy.Offscreen` or increasing the size of
     * the composable to accommodate the halo.
     *
     * @param color The color of the halo shadow. The opacity of this color will be
     *        adjusted across different points in the gradient to simulate the shadow
     *        fading effect.
     * @param blurRadius The blur radius of the halo shadow. If set to zero or negative,
     *        no blur will be applied.
     * @param haloBorderWidth The width of the halo shadow effect. This defines how far
     *        the halo extends from the inner circle to the outer edge.
     * @param innerCircleContentSize The size of the inner circle content. This defines
     *        the diameter of the central part inside the shadow.
     *
     * @return A [Modifier] with the circular shadow effect applied.
     */
    fun Modifier.drawOutlineHaloShadowBlur(
        color: Color,
        blurRadius: Dp,
        haloBorderWidth: Dp,
        innerCircleContentSize: Dp,
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
        this // No graphicsLayer or drawBehind applied
    }

}
