package org.mjdev.safedialer.extensions

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.content.ContextWrapper
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.DIPropertyDelegateProvider
import kotlin.reflect.KProperty

object DiExt {

    fun <T : Context> T.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ): DIPropertyDelegateProvider<Any?> = closestDI({ this }, mockBuilder)

    fun AbstractThreadedSyncAdapter.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ): DIPropertyDelegateProvider<Any?> = closestDI(
        { context },
        mockBuilder
    )

    fun Activity.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ) : DIPropertyDelegateProvider<Any?> = closestDI { mockBuilder(this) }

    fun Service.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ) : DIPropertyDelegateProvider<Any?> = closestDI { mockBuilder(this) }

    fun Application.closestDI(
        mockBuilder: (Context) -> DI? = { null }
    ) : DIPropertyDelegateProvider<Any?> = closestDI { mockBuilder(this) }

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
