package org.mjdev.safedialer.repository.base

import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.mjdev.safedialer.providers.core.AbstractProvider

@Suppress("CanBeParameter")
class EntityContentObserver(
    val provider: AbstractProvider,
    val handler: Handler = Handler(Looper.getMainLooper()),
    val onChanged: (uri: Uri?) -> Unit
) : ContentObserver(handler) {
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
        private val TAG = EntityContentObserver::class.simpleName

        fun entityContentObserver(
            provider: AbstractProvider,
            onChanged: (uri: Uri?) -> Unit
        ) = EntityContentObserver(
            provider = provider,
            onChanged = onChanged
        )
    }
}