package org.mjdev.safedialer.di

import android.R
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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.mjdev.safedialer.dao.DAO
import org.mjdev.safedialer.data.ContactsRepository
import org.mjdev.safedialer.helpers.Cache
import org.mjdev.safedialer.helpers.PreferencesManager
import org.mjdev.safedialer.service.IncomingCallService.Companion.CHANNEL_ID
import org.mjdev.safedialer.service.calls.IncomingCallBroadcastReceiver
import org.mjdev.safedialer.service.command.ServiceCommandReceiver
import org.mjdev.safedialer.service.external.PhoneLookup
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
val appModule = DI.Module("AppModule") {
    bindSingleton<ConnectivityManager> {
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
    bindSingleton<CoroutineScope> {
        CoroutineScope(IO + Job())
    }
    bindSingleton<Cache> {
        Cache()
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
    bindSingleton<OkHttpClient> {
        OkHttpClient.Builder()
            .callTimeout(60000, TimeUnit.MILLISECONDS)
            .build()
    }
    bindProvider<PreferencesManager> {
        PreferencesManager(
            context = instance()
        ).setName("app_preferences")
            .setMode(MODE_PRIVATE)
            .init()
    }
    bindSingleton<ContactsRepository> {
        ContactsRepository(
            context = instance(),
            scope = instance(),
            cache = instance()
        )
    }
    bindProvider<IncomingCallBroadcastReceiver> {
        IncomingCallBroadcastReceiver()
    }
    bindProvider<ServiceCommandReceiver> {
        ServiceCommandReceiver()
    }
    bindConstant<Int>("callCapabilities") {
        CallsManager.CAPABILITY_BASELINE or CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING
    }
    bindProvider<Notification>("notification") {
        instance<Context>().let { context ->
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Sledování hovorů")
                .setContentText("Služba běží na pozadí a sleduje příchozí hovory.")
                .setSmallIcon(R.drawable.sym_call_incoming)
                .build()
        }
    }
    bindConstant<NotificationChannel>("notificationChannel") {
        NotificationChannel(
            CHANNEL_ID,
            "Sledování hovorů",
            NotificationManager.IMPORTANCE_LOW,
        )
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