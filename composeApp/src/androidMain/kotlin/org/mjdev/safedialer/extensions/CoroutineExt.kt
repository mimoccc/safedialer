package org.mjdev.safedialer.extensions

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("unused")
object CoroutineExt {

    fun <T : R, R> Flow<T>.collectAsState(
        initial: R,
        context: CoroutineContext = EmptyCoroutineContext,
    ): State<R> = produceState(initial, this, context) {
        if (context == EmptyCoroutineContext) {
            collect { value = it }
        } else withContext(context) { collect { value = it } }
    }

    fun <T> produceState(
        initialValue: T,
        key1: Any?,
        key2: Any?,
        producer: suspend ProduceStateScope<T>.() -> Unit,
    ): State<T> {
        val result = mutableStateOf(initialValue)
        runBlocking {
            ProduceStateScopeImpl(result, coroutineContext).producer()
        }
        return result
    }

    class ProduceStateScopeImpl<T>(
        state: MutableState<T>,
        override val coroutineContext: CoroutineContext,
    ) : ProduceStateScope<T>, MutableState<T> by state {
        override suspend fun awaitDispose(onDispose: () -> Unit): Nothing {
            try {
                suspendCancellableCoroutine<Nothing> {}
            } finally {
                onDispose()
            }
        }
    }

}
