package org.mjdev.safedialer.extensions

import android.app.Activity
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProvider
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.DIPropertyDelegateProvider
import org.mjdev.safedialer.sync.email.ProviderEmails.Companion.EmailsProviderAuth
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.reflect.KProperty

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

    fun Path.createIfNoExists(): Path = runCatching {
        if (!exists()) createDirectories()
        return this
    }.getOrElse { e ->
        e.printStackTrace()
        this
    }

    fun ContentProvider.submitOnChangeEvent(
        id: Long? = null,
        auth: String? = context?.EmailsProviderAuth
    ) = if (auth == null) throw (RuntimeException("Empty auth can not be updated."))
    else CoroutineScope(Dispatchers.Default).launch {
        val uriPath = if (id != null) "content://$auth/$id" else "content://$auth"
        val uri = uriPath.toUri()
        withContext(Dispatchers.Main) {
            context!!.contentResolver.notifyChange(uri, null)
        }
    }

    fun runAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        scope: CoroutineScope = CoroutineScope(context),
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    @Composable
    fun rememberAssetImage(
        name: String = "avatar_yellow.png",
    ): ImageBitmap {
        val context: Context = LocalContext.current
        return remember(name) {
            context.assets.open(name).use { inputStream ->
                BitmapFactory.decodeStream(inputStream).asImageBitmap()
            }
        }
    }

    fun Activity.stringResource(
        @StringRes id: Int
    ) :  Lazy<String> = lazy{
        baseContext.resources.getString(id)
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
