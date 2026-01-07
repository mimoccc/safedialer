package org.mjdev.safedialer.extensions

import android.net.Uri
import androidx.compose.foundation.gestures.ScrollableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("unused")
object CustomExt {

    val isPreview
        get() = isLayoutLib() // LocalInspectionMode.current

    val isInPreviewMode: Boolean
        get() = isLayoutLib()

    fun isLayoutLib(): Boolean {
        val device = android.os.Build.DEVICE
        val product = android.os.Build.PRODUCT
        return device == "layoutlib" || product == "layoutlib"
    }

    val ScrollableState.canScroll
        get() = canScrollForward || canScrollBackward

    fun runAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        scope: CoroutineScope = CoroutineScope(context),
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    fun Uri.ifEmpty(
        block: () -> Uri
    ) = if (this == Uri.EMPTY) block() else this

    fun Long.ifZero(
        block: () -> Long
    ) = if (this == 0L) block() else this

    fun <T> T?.ifNull(
        block: () -> T?
    ) = this ?: block()

}
