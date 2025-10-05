package org.mjdev.safedialer.di

import android.app.Application
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.telecom.CallsManager
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.dao.DAO
import org.mjdev.safedialer.extensions.CustomExt.isInPreviewMode
import org.mjdev.safedialer.helpers.InvalidContextException
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.providers.custom.email.MailClient
import org.mjdev.safedialer.repository.DataRepository
import org.mjdev.safedialer.repository.IDataRepository
import org.mjdev.safedialer.repository.MockDataRepository
import org.mjdev.safedialer.service.IncomingCallService.Companion.CHANNEL_ID
import org.mjdev.safedialer.service.calls.IncomingCallBroadcastReceiver
import org.mjdev.safedialer.service.command.ServiceCommandReceiver
import org.mjdev.safedialer.service.external.PhoneLookup
import java.io.File
import java.util.Properties
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
val appModule = DI.Module("AppModule") {
    bindConstant<Int>("callCapabilities") {
        CallsManager.CAPABILITY_BASELINE or CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING
    }
    bindConstant<NotificationChannel>("notificationChannel") {
        NotificationChannel(
            CHANNEL_ID,
            "Sledování hovorů", // todo strings from resources
            NotificationManager.IMPORTANCE_LOW,
        )
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
    bindSingleton<ConnectivityManager> {
        instance<Context>()
            .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    bindSingleton<NotificationManager> {
        instance<Context>()
            .getSystemService(NotificationManager::class.java) as NotificationManager
    }
    bindSingleton<WindowManager> {
        instance<Context>()
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    bindSingleton<KeyguardManager> {
        instance<Context>()
            .getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    }
    bindSingleton<CoroutineScope> {
        CoroutineScope(Dispatchers.IO + Job())
    }
    bindSingleton<CallsManager> {
        CallsManager(instance())
    }
    bindSingleton<DAO> {
        DAO(instance())
    }
    bindSingleton<PhoneLookup> {
        PhoneLookup(instance())
    }
    bindSingleton<okhttp3.Cache> {
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
        okhttp3.Cache(
            directory = cacheDir,
            maxSize = 1024L * 1024L * 1024L // 1GB
        )
    }
    bindSingleton<MailClient> {
        MailClient(
            hostImap = BuildConfig.SERVER,
            hostSmtp = BuildConfig.SERVER,
            portImap = BuildConfig.SERVER_PORT_IMAP.toInt(),
            portSmtp = BuildConfig.SERVER_PORT_SMTP.toInt(),
            userImap = BuildConfig.SERVER_UNAME,
            passwordImap = BuildConfig.SERVER_UPASS,
            userSmtp = BuildConfig.SERVER_UNAME,
            passwordSmtp = BuildConfig.SERVER_UPASS,
            props = Properties(),
        )
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
                .okHttpClient { okhttpClient }
                .crossfade(false)
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir)
                        .build()
                }
                .components {
                    // Add any custom components here if needed
                }
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(0.5)
                        .build()
                }
                .build()
        }
    }
    bindSingleton<OkHttpClient> {
        val context: Context = instance()
        if (isInPreviewMode) {
            throw (InvalidContextException(context))
        } else {
            OkHttpClient.Builder()
                .cache(instance())
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .callTimeout(60000, TimeUnit.MILLISECONDS)
                .build()
        }
    }
    bindSingleton<IDataRepository> {
        if (isInPreviewMode) {
            MockDataRepository(
                context = instance(),
                scope = instance(),
            ).apply {
                preloadContacts()
            }
        } else {
            DataRepository(
                context = instance(),
                scope = instance(),
            ).apply {
                preloadContacts()
            }
        }
    }
    bindSingleton<Notification>("notification") {
        instance<Context>().let { context ->
            NotificationCompat.Builder(context, CHANNEL_ID)
                // todo resources
                .setContentTitle("Sledování hovorů")
                // todo resources
                .setContentText("Služba běží na pozadí a sleduje příchozí hovory.")
                .setSmallIcon(android.R.drawable.sym_call_incoming)
                .build()
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
}
