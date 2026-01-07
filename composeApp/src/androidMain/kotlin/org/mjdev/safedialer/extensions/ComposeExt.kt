package org.mjdev.safedialer.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import kotlinx.coroutines.flow.flow
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.DiExt.closestDI

@Suppress("unused")
object ComposeExt {

    val isLandscape: Boolean
        @Composable
        get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait: Boolean
        @Composable
        get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

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
    fun rememberAssetImage(
        name: String = "avatar1.png",
    ): ImageBitmap {
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

    @Composable
    fun rememberImageLoader(): ImageLoader {
        val context: Context = LocalContext.current
        return remember {
            val di by context.closestDI { mainDI(context) }
            val imageLoader: ImageLoader by di.instance()
            imageLoader
        }
    }

}
