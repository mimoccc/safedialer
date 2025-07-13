package org.mjdev.safedialer.extensions

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.mjdev.safedialer.helpers.Cache

typealias MapFilter<T> = (Map<String, List<T>>, String) -> Map<String, List<T>>

object CursorFlow {

    val EmptyCursor: Cursor
        get() = MatrixCursor(arrayOf())

    val query: (
        contentResolver: ContentResolver,
        uri: Uri
    ) -> Cursor = { contentResolver, uri ->
        runCatching {
            contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
        }.onFailure { e ->
            e.printStackTrace()
        }.getOrNull() ?: EmptyCursor
    }

    inline fun <reified T : Any> cursorFlow(
        context: Context,
        uri: Uri,
        cache: Cache = Cache(),
        contentResolver: ContentResolver = context.contentResolver,
        crossinline block: suspend (uri: Uri, cursor: Cursor) -> T,
    ) = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                launch {
                    block(uri, query(contentResolver, uri)).also { data ->
                        cache[T::class] = data
                        trySend(data)
                    }
                }
            }
        }
        runCatching {
            contentResolver.registerContentObserver(uri, true, observer)
        }.onFailure { e ->
            e.printStackTrace()
        }
        if (cache.contains<T>().not()) {
            val cursor = query(contentResolver, uri)
            block(uri, cursor).also { data ->
                cache[T::class] = data
                trySend(data)
            }
            cursor.close()
        } else {
            trySend(cache[T::class] as T)
        }
        awaitClose {
            runCatching {
                contentResolver.unregisterContentObserver(observer)
            }.onFailure { e ->
                e.printStackTrace()
            }
        }
    }

}
