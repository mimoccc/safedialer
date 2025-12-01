package org.mjdev.safedialer.repository.base

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Entity
import kotlin.reflect.full.companionObjectInstance

abstract class ADataRepository(
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : DIAware {
    override val di: DI by context.closestDI { mainDI(context) }

    inline fun <reified E : AbstractProvider, reified T : Entity> providerObserver(
        provider: E,
        crossinline block: suspend E.() -> List<T>
    ) = callbackFlow {
        val companion = T::class.companionObjectInstance as? Entity.CompanionWithUri
            ?: error("Entity object must implement CompanionWithUri")
        val uri: Uri = companion.uri
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                scope.launch {
                    val entities = runCatching {
                        block(provider)
                    }.getOrElse { exception ->
                        exception.printStackTrace()
                        emptyList()
                    }
                    trySend(entities)
                }
            }
        }
        runCatching {
            if (uri != Uri.EMPTY) {
                provider.registerContentObserver(uri, observer)
            } else {
                Log.e(TAG, "Got empty uri. No observer registered.")
            }
        }
        observer.onChange(false)
        awaitClose {
            runCatching {
                context.contentResolver.unregisterContentObserver(observer)
            }
        }
    }

    companion object {
        val TAG = ADataRepository::class.simpleName
    }
}