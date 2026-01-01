package org.mjdev.safedialer.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.annotation.ColorLong
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil3.ImageLoader
import kotlinx.coroutines.flow.flow
import org.kodein.di.LazyDI
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import org.kodein.di.direct
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI

@Suppress("unused")
object ComposeExt {

    val isLandscape: Boolean
        @Composable
        get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait: Boolean
        @Composable
        get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    fun Modifier.applyIf(
        condition: Boolean,
        other: Modifier.() -> Modifier
    ): Modifier = if (condition) this.then(other()) else this

    val ScrollableState.canScroll
        get() = canScrollForward || canScrollBackward

    class LazyViewModelProxy<T>(override val value: T) : Lazy<T> {
        override fun isInitialized(): Boolean = true
    }

    fun ComponentActivity.enableEdgeToEdge(
        statusBarColor: Color = Color.DarkGray,
        navigationBarColor: Color = Color.DarkGray,
    ) = enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(statusBarColor.toColorInt()),
        navigationBarStyle = SystemBarStyle.dark(navigationBarColor.toColorInt())
    )

    @Composable
    fun rememberCurrentSize(): State<DpSize> {
        val view: View = LocalView.current
        return remember(view.width, view.height) {
            derivedStateOf {
                DpSize(view.width.dp, view.height.dp)
            }
        }
    }

    @Composable
    fun Modifier.dashedBorder(
        width: Dp,
        radius: Dp,
        color: Color
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

    @ColorLong
    fun Color.toColorLong(): Long {
        return if ((value and 0x3FUL) < 16UL) {
            value
        } else {
            (value and 0x3FUL.inv()) or ((value and 0x3FUL) - 1UL)
        }.toLong()
    }

    @ColorInt
    fun Color.toColorInt(): Int {
        return if ((value and 0x3FUL) < 16UL) {
            value
        } else {
            (value and 0x3FUL.inv()) or ((value and 0x3FUL) - 1UL)
        }.toInt()
    }

    @Composable
    fun rememberAssetImage(
        name: String = "avatar1.png",
    ) : ImageBitmap {
        val context: Context = LocalContext.current
        return remember {
            context.assets.open(name).use { inputStream ->
                BitmapFactory.decodeStream(inputStream).asImageBitmap()
            }
        }
    }

    @Suppress("ParamsComparedByRef")
    @Composable
    fun <T> collectAsState(
        key: Any? = Unit,
        initial: T? = null,
        block: suspend () -> T?
    ) = remember(key) {
        flow {
            emit(block())
        }
    }.collectAsState(initial)

    @Suppress("ParamsComparedByRef")
    @Composable
    inline fun <reified VM : ViewModel> rememberViewModelSafe(
        key: Any? = null,
        context: Context = LocalContext.current,
        localDi: LazyDI? = mainDI(context), // todo : remove?
        crossinline mockModelFactory: (Context) -> VM // todo ???
    ): Lazy<VM> {
        val viewModelStoreOwner = LocalViewModelStoreOwner.current
        return remember {
            if (localDi == null || viewModelStoreOwner == null) {
                LazyViewModelProxy(mockModelFactory(context))
            } else {
                ViewModelLazy(
                    viewModelClass = VM::class,
                    storeProducer = { viewModelStoreOwner.viewModelStore },
                    factoryProducer = {
                        object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return localDi.direct.instance<VM>(key) as T
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun rememberImageLoader(): ImageLoader {
        val context: Context = LocalContext.current
        return remember {
            val di by context.closestDI { mainDI(context) }
            val imageLoader: ImageLoader by di.instance()
            imageLoader
        }
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
     * "drawOutlineCircularShadowGradient" for an example of how to implement the
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
