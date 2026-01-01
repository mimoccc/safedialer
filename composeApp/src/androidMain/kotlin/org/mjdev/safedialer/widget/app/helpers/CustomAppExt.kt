package org.mjdev.safedialer.widget.app.helpers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.glance.GlanceComposable
import androidx.glance.LocalContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.android.closestDI
import org.kodein.di.direct
import org.kodein.di.instance
import org.mjdev.safedialer.data.User
import org.mjdev.safedialer.extensions.ImageBitmapExt.resize

object CustomAppExt {

    @Composable
    @GlanceComposable
    fun rememberCurrentUser(
        coroutineContext: CoroutineDispatcher = Dispatchers.Main,
        pictureWidth: Dp = Dp.Unspecified,
        pictureHeight: Dp = Dp.Unspecified,
    ): State<User> {
        val context = LocalContext.current
        val di by closestDI({ context.applicationContext })
        return remember {
            val userFlow = di.direct.instance<Flow<User>>()
            userFlow.map { uf ->
                if (pictureWidth != Dp.Unspecified || pictureHeight != Dp.Unspecified) {
                    uf.copy(
                        picture = uf.picture?.resize(pictureWidth, pictureHeight)
                    )
                } else uf
            }
        }.collectAsState(User(), coroutineContext)
    }

}