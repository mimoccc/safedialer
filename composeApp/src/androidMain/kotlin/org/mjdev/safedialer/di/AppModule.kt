package org.mjdev.safedialer.di

import android.app.Application
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.res.Resources
import android.net.ConnectivityManager
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.telecom.CallsManager
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.i18n.phonenumbers.PhoneNumberUtil
import ezvcard.VCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.Cache
import okhttp3.logging.HttpLoggingInterceptor
import okio.Path.Companion.toPath
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.R
import org.mjdev.safedialer.data.User
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.helpers.VCFHelper.toImageBitmap
import org.mjdev.safedialer.repository.DataRepository
import org.mjdev.safedialer.repository.base.IDataRepository
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.service.calls.IncomingCallService.Companion.CHANNEL_ID
import org.mjdev.safedialer.service.calls.IncomingCallBroadcastReceiver
import org.mjdev.safedialer.service.calls.command.ServiceCommandReceiver
import org.mjdev.safedialer.service.external.PhoneLookup
import org.mjdev.safedialer.webdav.WebDavClient
import java.io.File
import java.util.concurrent.TimeUnit

const val CAPABILITIES_CALL = "callCapabilities"
const val NOTIFICATION_CHANNEL = "notificationChannel"
const val NOTIFICATION_TAG = "notification"

@Suppress("DEPRECATION")
val appModule = DI.Module("AppModule") {
    // constants
    bindConstant<Int>(CAPABILITIES_CALL) {
        CallsManager.CAPABILITY_BASELINE or CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING
    }
    // providers
    bindProvider<Resources> {
        val context: Context = instance()
        context.resources
    }
    bindProvider<Application> {
        val context = instance<Context>()
        (context.applicationContext as? Application)
            ?: error("Context is not an Application instance.")
    }
    bindProvider<PreferencesManager> {
        PreferencesManager(
            context = instance()
        ).setName("app_preferences")
            .setMode(MODE_PRIVATE)
            .init()
    }
    bindProvider<IncomingCallBroadcastReceiver> {
        IncomingCallBroadcastReceiver()
    }
    bindProvider<ServiceCommandReceiver> {
        ServiceCommandReceiver()
    }
    bindProvider<Cache> {
        val context: Context = instance()
        val systemCachePath = System.getProperty("java.io.tmpdir")
        val systemCacheDir = File(systemCachePath, "http_cache")
        val cacheDir = runCatching {
            context.cacheDir?.let {
                File(it, "http_cache")
            }
        }.onFailure { e ->
            e.printStackTrace()
        }.getOrNull() ?: systemCacheDir
        Cache(
            directory = cacheDir,
            maxSize = 1024L * 1024L * 1024L // 1GB
        )
    }
    bindProvider<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().apply {
            setLevel(
                if (BuildConfig.IS_DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            )
        }
    }
    bindProvider<WebDavClient> {
        WebDavClient(instance())
    }
    bindProvider<OkHttpClient> {
        val logging: HttpLoggingInterceptor = instance()
        if (isInPreviewMode) {
            OkHttpClient.Builder()
                .followRedirects(true)
                .addInterceptor(logging)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(instance())
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .callTimeout(60000, TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .addInterceptor(logging)
                .build()
        }
    }
    bindProvider<ConnectivityManager> {
        instance<Context>()
            .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    bindProvider<NotificationManager> {
        instance<Context>()
            .getSystemService(NotificationManager::class.java) as NotificationManager
    }
    bindProvider<WindowManager> {
        instance<Context>()
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    bindProvider<KeyguardManager> {
        instance<Context>()
            .getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }
    bindProvider<CoroutineScope> {
        CoroutineScope(Dispatchers.IO + Job())
    }
    bindProvider<NotificationChannel>(NOTIFICATION_CHANNEL) {
        val resources = instance<Resources>()
        val title = resources.getString(R.string.label_watching_calls)
        NotificationChannel(
            CHANNEL_ID,
            title,
            NotificationManager.IMPORTANCE_LOW,
        )
    }
    bindProvider<Notification>(NOTIFICATION_TAG) {
        val context: Context = instance<Context>()
        val title = context.getString(R.string.label_watching_calls)
        val description = context.getString(R.string.notification_text)
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }
    // singletons
    bindSingleton<CallsManager> {
        CallsManager(instance())
    }
    bindSingleton<PhoneLookup> {
        PhoneLookup(instance())
    }
    bindSingleton<ImageLoader> {
        val context: Context = instance()
        val systemCachePath = System.getProperty("java.io.tmpdir")
        val systemCacheDir = File(systemCachePath, "image_cache")
        val cacheDir = runCatching {
            context.cacheDir?.let {
                File(it, "http_cache")
            }
        }.onFailure { e ->
            e.printStackTrace()
        }.getOrNull() ?: systemCacheDir
        if (isInPreviewMode) {
            ImageLoader.Builder(context)
                .crossfade(false)
                .build()
        } else {
            val okhttpClient: OkHttpClient = instance()
            ImageLoader.Builder(context)
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.absolutePath.toPath())
                        .build()
                }
                .components {
                    // Add any custom components here if needed
                    add(SvgDecoder.Factory())
                    add(OkHttpNetworkFetcherFactory(
                        callFactory = { okhttpClient }
                    ))
                }
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(context, 0.5)
                        .build()
                }
                .crossfade(false)
                .build()
        }
    }
    bindSingleton<IDataRepository> {
        if (isInPreviewMode) {
            MockDataRepository(
                context = instance(),
                scope = instance(),
            )
        } else {
            DataRepository(
                context = instance(),
                scope = instance(),
            )
        }
    }
    bindSingleton<Gson> {
        GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
    }
    bindSingleton<PhoneNumberUtil> {
        PhoneNumberUtil.getInstance()
    }
    bindProvider<Flow<User>> {
        val webDavClient: WebDavClient = instance()
        webDavClient.userVCard.map { vcard: VCard? ->
            User(
                picture = vcard?.photos?.firstOrNull()?.toImageBitmap(),
                name = vcard?.formattedName?.value ?: "-",
                emails = vcard?.emails?.map { e -> e.value } ?: emptyList()
            )
        }
    }
}
