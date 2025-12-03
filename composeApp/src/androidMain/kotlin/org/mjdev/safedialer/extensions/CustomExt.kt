package org.mjdev.safedialer.extensions

import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.DIPropertyDelegateProvider
import kotlin.reflect.KProperty

@Suppress("MemberVisibilityCanBePrivate")
object CustomExt {

    val isPreview
        @Composable
        get()= LocalInspectionMode.current

    val isInPreviewMode: Boolean
        get() = isLayoutLib()

    fun isLayoutLib(): Boolean {
        val device = android.os.Build.DEVICE
        val product = android.os.Build.PRODUCT
        return device == "layoutlib" || product == "layoutlib"
    }

    fun <T : Context> T.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ): DIPropertyDelegateProvider<Any?> = closestDI({ this }, mockBuilder)

    fun AbstractThreadedSyncAdapter.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ): DIPropertyDelegateProvider<Any?> = closestDI(
        { context },
        mockBuilder
    )

    fun Uri.ifEmpty(
        block: () -> Uri
    ) = if (this == Uri.EMPTY) block() else this

    fun Long.ifZero(
        block: () -> Long
    ) = if (this == 0L) block() else this

    fun <T> T?.ifNull(
        block: () -> T?
    ) = this ?: block()

//    fun closestDI(
//        context: Context,
//        mockBuilder: (Context) -> DI? = { null }
//    ): DIPropertyDelegateProvider<Any?> = LazyContextDIPropertyDelegateProvider(
//        { context },
//        mockBuilder
//    )

    fun closestDI(
        getContext: () -> Context,
        mockBuilder: (Context) -> DI? = { null }
    ): DIPropertyDelegateProvider<Any?> = LazyContextDIPropertyDelegateProvider(
        getContext,
        mockBuilder
    )

    class LazyContextDIPropertyDelegateProvider(
        val getContext: () -> Context,
        val mockBuilder: (Context) -> DI? = { null }
    ) : DIPropertyDelegateProvider<Any?> {
        override operator fun provideDelegate(
            thisRef: Any?,
            property: KProperty<*>?
        ): Lazy<DI> = lazy {
            runCatching<DI?> {
                getContext().let { context ->
                    (context.applicationContext as? DIAware)?.di
                        ?: ((context as? ContextWrapper)?.baseContext as? DIAware)?.di
                        ?: mockBuilder(context)
                }
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull() ?: throw (RuntimeException("Can not instantiate di container."))
        }
    }
}
