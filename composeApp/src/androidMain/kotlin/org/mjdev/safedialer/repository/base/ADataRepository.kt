package org.mjdev.safedialer.repository.base

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.repository.base.EntityContentObserver.Companion.entityContentObserver

// todo : remove ?
abstract class ADataRepository(
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job()),
) : IDataRepository, DIAware {
    override val di: DI by context.closestDI { mainDI(context) }

    inline fun <reified E : AbstractProvider, reified T : Entity> providerFlow(
        provider: E,
        crossinline block: suspend E.() -> List<T>
    ) = callbackFlow {
        val uris = provider.getUris()
        if (uris.isEmpty()) {
            Log.w(TAG, "No uri for changes for: ${provider::class.simpleName}")
        }
        val observer = entityContentObserver(provider) { uri ->
            Log.d(TAG, "Got changes for: ${provider::class.simpleName}, uri: $uri")
            scope.launch {
                val entities = runCatching {
                    block(provider)
                }.getOrElse { exception ->
                    Log.e(TAG, "Error in providerFlow block: ${exception.message}", exception)
                    emptyList()
                }
                if (entities.isNotEmpty()) {
                    Log.d(
                        TAG,
                        "Emitting changes (${entities.size}) ${provider::class.simpleName}, uri: $uri"
                    )
                    trySend(entities)
                } else {
                    Log.d(TAG, "No entities ${provider::class.simpleName}, uri: $uri")
                }
            }
        }
        observer.register()
        observer.onChange(false)
        awaitClose {
            observer.unregister()
        }
    }

    companion object {
        val TAG = ADataRepository::class.simpleName
    }
}