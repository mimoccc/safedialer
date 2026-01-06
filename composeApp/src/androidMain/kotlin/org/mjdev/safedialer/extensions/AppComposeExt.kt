package org.mjdev.safedialer.extensions

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.kodein.di.DI
import org.kodein.di.compose.LocalDI
import org.kodein.di.direct
import org.kodein.di.instance
import org.mjdev.safedialer.data.User
import org.mjdev.safedialer.extensions.ImageBitmapExt.resize

@Suppress("unused")
object AppComposeExt {

    private val TAG = AppComposeExt::class.simpleName

    @Composable
    fun rememberCurrentUser(
        context: CoroutineDispatcher = Dispatchers.Main,
        pictureWidth: Dp = Dp.Unspecified,
        pictureHeight: Dp = Dp.Unspecified,
    ): State<User> {
        val di: DI? = LocalDI.current
        return remember {
            val userFlow = di?.direct?.instance<Flow<User>>()
            if (userFlow == null) {
                Log.w(TAG, "User flow empty. Emitting empty user.")
                flow {
                    emit(User().copy())
                }
            } else {
                userFlow.map { uf ->
                    if (pictureWidth != Dp.Unspecified || pictureHeight != Dp.Unspecified) {
                        uf.copy(
                            picture = uf.picture?.resize(pictureWidth, pictureHeight)
                        )
                    } else uf
                }
            }
        }.collectAsState(User(), context)
    }

    @Composable
    fun <T> rememberMapFilter(
        map: Map<String, List<T>>,
        filterText: String,
        ignoreCase : Boolean = true,
        comparer: (T, String) -> Boolean = { i, s ->
            i.toString().contains(s, ignoreCase)
        },
    ) = remember(map, filterText) {
        flow<Map<String, List<T>>> {
            val s = filterText.trim()
            if (s.isEmpty() || map.isEmpty())
                emit(map)
            else {
                map.map { (label, data) ->
                    label to data.filter { i ->
                        comparer(i, filterText)
                    }
                }.toMap().filter { (_, data) ->
                    data.isNotEmpty()
                }.also { m ->
                    emit(m)
                }
            }
        }
    }.collectAsState(emptyMap())

}
