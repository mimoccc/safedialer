package org.mjdev.safedialer.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.kodein.di.LazyDI
import org.kodein.di.direct
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI

@Suppress("unused")
object ViewModelExt {

    class LazyViewModelProxy<T>(override val value: T) : Lazy<T> {
        override fun isInitialized(): Boolean = true
    }

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

}
