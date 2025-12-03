package org.mjdev.safedialer.repository.base

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
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
import org.mjdev.safedialer.repository.base.ADataRepository.Companion.TAG
import org.mjdev.safedialer.repository.base.EntityContentObserver.Companion.entityContentObserver
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class EntityContentObserver(
    val provider: AbstractProvider,
    val onChanged: (uri: Uri?) -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {
    private val uris: List<Uri>
        get() = provider.getUris()

    override fun onChange(
        selfChange: Boolean
    ) {
        onChanged(null)
    }

    override fun onChange(
        selfChange: Boolean,
        uri: Uri?
    ) {
        onChanged(uri)
    }

    override fun onChange(
        selfChange: Boolean,
        uri: Uri?,
        flags: Int
    ) {
        onChanged(uri)
    }

    override fun onChange(
        selfChange: Boolean,
        uris: MutableCollection<Uri>,
        flags: Int
    ) {
        uris.forEach { uri ->
            onChanged(uri)
        }
    }

    fun register() = runCatching {
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                Log.d(TAG, "Registering changes observer for uri: $uri")
                provider.registerContentObserver(
                    uri,
                    this@EntityContentObserver,
                    true
                )
            }
        } else {
            Log.w(TAG, "Got empty uri. No observer registered.")
        }
    }

    fun unregister() = runCatching {
        provider.unregisterContentObserver(this)
    }

    companion object {
        fun entityContentObserver(
            provider: AbstractProvider,
            onChanged: (uri: Uri?) -> Unit
        ) = EntityContentObserver(
            provider = provider,
            onChanged = onChanged
        )
    }
}

abstract class ADataRepository(
    val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job()),
) : DIAware {
    override val di: DI by context.closestDI { mainDI(context) }

    fun runAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    inline fun <reified E : AbstractProvider, reified T : Entity> providerFlow(
        provider: E,
        crossinline block: suspend E.() -> List<T>
    ) = callbackFlow {
        val uris = provider.getUris()
        if (uris.isEmpty()) {
            Log.w(TAG, "No uri for changes for: ${provider::class.simpleName}")
        }
        val observer = entityContentObserver(provider) { uri ->
            Log.d(TAG, "Got changes of: $uri")
            runAsync {
                val entities = runCatching {
                    block(provider)
                }.getOrElse { exception ->
                    Log.e(TAG, exception.message, exception)
                    emptyList()
                }
                Log.d(TAG, "Emitting changes (${entities.size}) for : $uri")
                send(entities)
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