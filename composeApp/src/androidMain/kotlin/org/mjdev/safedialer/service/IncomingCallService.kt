package org.mjdev.safedialer.service

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.ktor.client.content.LocalFileContent
import org.mjdev.safedialer.dao.DAO
import org.mjdev.safedialer.data.model.MetaData
import org.mjdev.safedialer.service.calls.CallListener
import org.mjdev.safedialer.service.calls.IncomingCallBroadcastReceiver
import org.mjdev.safedialer.service.command.CommandReceiver
import org.mjdev.safedialer.service.command.ServiceCommand
import org.mjdev.safedialer.service.command.ServiceCommandReceiver
import org.mjdev.safedialer.ui.components.ContactDetail
import org.mjdev.safedialer.window.ComposeFloatingWindow
import org.mjdev.safedialer.window.ComposeFloatingWindow.Companion.alertLayoutParams

@Suppress("DEPRECATION")
class IncomingCallService : Service(), CallListener, CommandReceiver {
    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java) as NotificationManager
    }
    private val notification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Sledování hovorů")
            .setContentText("Služba běží na pozadí a sleduje příchozí hovory.")
            .setSmallIcon(R.drawable.sym_call_incoming)
            .build()
    }
    private val incomingCallReceiver by lazy {
        IncomingCallBroadcastReceiver()
    }
    private val commandsReceiver by lazy {
        ServiceCommandReceiver()
    }
    private val channel = NotificationChannel(
        CHANNEL_ID,
        "Sledování hovorů",
        NotificationManager.IMPORTANCE_LOW
    )
    private val canDrawOverlays: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    private val dao by lazy { DAO.getInstance(this) }

    override fun onCreate() {
        isStarted = true
        super.onCreate()
        notificationManager.createNotificationChannel(channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
        } else {
            startForeground(1, notification)
        }
        if (!canDrawOverlays) {
            requestOverlayPermission()
            serviceStop(true)
        }
        runCatching {
            incomingCallReceiver.register(this)
        }.onFailure { e ->
            e.printStackTrace()
            serviceStop(true)
        }
        runCatching {
            commandsReceiver.register(this)
        }.onFailure { e ->
            e.printStackTrace()
            serviceStop(true)
        }
        runCatching {
            dao.meta.clear()
            dao.meta.add(MetaData("test", "test"))
            dao.meta.asList<MetaData>().forEach { o ->
                Log.d("DAO", o.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            incomingCallReceiver.unregister(this)
        }.onFailure { e ->
            e.printStackTrace()
        }
        runCatching {
            commandsReceiver.unregister(this)
        }.onFailure { e ->
            e.printStackTrace()
        }
        if (isRestart) {
            start(this)
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null

    private fun serviceStop(
        restart: Boolean
    ) {
        isRestart = restart
        lastAlerts.forEach { la -> la.hide() }
        stopSelf()
    }

    private fun requestOverlayPermission() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    private fun showAlert(
        phoneNumber: String?,
        context: Context = applicationContext
    ) = ComposeFloatingWindow(
        context = context,
        windowParams = alertLayoutParams(context)
    ) {
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                ContactDetail(
                    caller = phoneNumber,
                    showCloseButton = true,
                )
            }
        }
        show()
        lastAlerts.add(this)
    }

    override fun onIncomingCall(incomingNumber: String?) { // number null
        lastAlerts.hideAll()
        showAlert(incomingNumber)
    }

    override fun onCallEnded(incomingNumber: String?) { // number null
        lastAlerts.hideAll()
    }

    override fun onCallAccepted(incomingNumber: String?) { // number ok
        lastAlerts.hideAll()
    }

    override fun onCommand(
        command: ServiceCommand?,
        data: Bundle?
    ) {
        when (command) {
            ServiceCommand.ShowAlert -> {
                data?.getString(EXTRA_INCOMING_NUMBER).also { phoneNumber ->
                    showAlert(phoneNumber)
                }
            }

            ServiceCommand.HideAlert -> lastAlerts.hideAll()
            ServiceCommand.Start -> start(this)
            ServiceCommand.Stop -> serviceStop(false)
            ServiceCommand.Restart -> serviceStop(true)
            else -> {}
        }
    }

    companion object {
        private const val CHANNEL_ID = "incoming_call_service_channel"
        private var lastAlerts = mutableListOf<ComposeFloatingWindow>()
        private var isRestart = false
        var isStarted = false

        fun start(context: Context) = runCatching {
            Intent(context, IncomingCallService::class.java).let { intent ->
                ContextCompat.startForegroundService(context, intent)
            }
        }.onFailure { e ->
            e.printStackTrace()
        }

        fun cmd(
            context: Context,
            cmd: ServiceCommand,
            data: Bundle? = null
        ) {
            context.sendBroadcast(Intent(ServiceCommandReceiver.ACTION).apply {
                putExtra(ServiceCommandReceiver.CMD, cmd.toString())
                putExtra(ServiceCommandReceiver.DATA, data)
            })
        }

        fun showAlert(context: Context, phoneNumber: String? = null) = cmd(
            context,
            ServiceCommand.ShowAlert,
            Bundle().apply {
                putString(EXTRA_INCOMING_NUMBER, phoneNumber)
            }
        )

        fun hideAlert(context: Context) = cmd(
            context,
            ServiceCommand.HideAlert,
            null
        )

        fun stop(context: Context) = cmd(
            context,
            ServiceCommand.Stop,
            null
        )

        fun restart(context: Context) = cmd(
            context,
            ServiceCommand.Restart,
            null
        )

        private fun MutableList<ComposeFloatingWindow>.hideAll() {
            iterator().apply {
                while (hasNext()) {
                    val sw = next()
                    sw.hide()
                    remove()
                }
            }
        }

    }
}

