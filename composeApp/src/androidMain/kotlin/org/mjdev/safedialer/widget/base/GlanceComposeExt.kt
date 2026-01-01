package org.mjdev.safedialer.widget.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.width
import kotlinx.coroutines.flow.flow
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode

@Suppress("unused")
object GlanceComposeExt {

    val PORTRAIT = DpSize(400.dp, 800.dp)
    val LANDSCAPE = DpSize(800.dp, 400.dp)

    @Composable
    fun rememberPreviewSize(
        isPortrait: Boolean = true,
        isInDesignMode: Boolean = isInPreviewMode,
        displaySize: DpSize? = if (isInDesignMode) if (isPortrait) PORTRAIT else LANDSCAPE else null
    ) = remember(displaySize) {
        derivedStateOf {
            DpSize(
                displaySize?.width ?: 0.dp,
                displaySize?.height ?: 0.dp
            )
        }
    }

    fun DpSize.isEmpty(): Boolean = (width == 0.dp && height == 0.dp)
    fun DpSize.isNotEmpty() = isEmpty().not()

    @Composable
    @GlanceComposable
    fun stringResource(
        resId: Int,
    ): String {
        val context: Context = LocalContext.current
        return remember(resId) { context.getString(resId) }
    }

    fun GlanceModifier.applyIf(
        condition: Boolean,
        block: GlanceModifier.() -> GlanceModifier
    ): GlanceModifier = if (condition) this.then(block()) else this

    fun GlanceModifier.size(
        size: DpSize
    ): GlanceModifier = width(size.width).height(size.height)

    fun GlanceModifier.applyPreviewSize(size: DpSize): GlanceModifier = applyIf(
        size.isNotEmpty()
    ) {
        size(size)
    }.applyIf(size.isEmpty()) {
        fillMaxSize()
    }

    internal val ActionEmpty: EmptyAction
        get() = EmptyAction()

    internal class EmptyAction : ActionCallback, Action {
        override suspend fun onAction(
            context: Context,
            glanceId: GlanceId,
            parameters: ActionParameters
        ) {
            // No-op
        }
    }

    fun Preferences.int(
        name: String,
        defValue: Int = 0
    ): Int = this[intPreferencesKey(name)] ?: defValue

    fun Preferences.bool(
        name: String,
        defValue: Boolean = false
    ): Boolean = this[booleanPreferencesKey(name)] ?: defValue

    val EmptyIcon
        @GlanceComposable
        @Composable
        get() = remember {
            Icon.createWithBitmap(
                createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                )
            )
        }

    val EmptyImageProvider: ImageProvider
        @GlanceComposable
        @Composable
        get() = remember {
            Icon.createWithBitmap(
                createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                )
            ).let { icon ->
                ImageProvider(icon)
            }
        }

    @GlanceComposable
    @Composable
    fun rememberVectorImageProvider(
        imageVector: ImageVector,
        size: Dp,
        tint: Color
    ): State<ImageProvider> {
        val context = LocalContext.current
        return remember(
            imageVector,
            size,
            tint
        ) {
            flow {
                val sizePx = (size.value * context.resources.displayMetrics.density).toInt()
                val bmp = createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                // todo from ImageVector
                canvas.drawColor(tint.toArgb())
                // /todo
                val icon = Icon.createWithBitmap(bmp)
                emit(ImageProvider(icon))
            }
        }.collectAsState(EmptyImageProvider)
    }

    @Composable
    fun <T> rememberDerivedState(
        key: Any,
        block: () -> T
    ) = remember(key, block) { derivedStateOf { block() } }

}
