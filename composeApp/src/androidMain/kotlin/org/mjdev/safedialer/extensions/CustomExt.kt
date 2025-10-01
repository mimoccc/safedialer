package org.mjdev.safedialer.extensions

import android.content.Context
import android.content.ContextWrapper
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.mjdev.safedialer.service.IncomingCallService

object CustomExt {

    val IS_DEBUG: Boolean = IncomingCallService.isStarted.not()

    fun Any.closestDI(
        context: Context,
        mockBuilder: () -> DI = { DI.lazy { } }
    ): DI = runCatching {
        (context as? DIAware)?.di
            ?: ((context as? ContextWrapper)?.baseContext as? DIAware)?.di
            ?: (context.applicationContext as? DIAware)?.di
            ?: mockBuilder()
    }.getOrElse {
        throw (RuntimeException("Can not instantiate di container."))
    }

}