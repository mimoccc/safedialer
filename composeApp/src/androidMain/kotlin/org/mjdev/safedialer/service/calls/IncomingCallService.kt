package org.mjdev.safedialer.service.calls

import android.app.Notification
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
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.DiExt.closestDI
import org.mjdev.safedialer.service.calls.command.CommandReceiver
import org.mjdev.safedialer.service.calls.command.ServiceCommand
import org.mjdev.safedialer.service.calls.command.ServiceCommandReceiver
import org.mjdev.safedialer.service.external.PhoneLookup
import org.mjdev.safedialer.sync.SyncManager
import org.mjdev.safedialer.ui.components.call.CallDialog
import org.mjdev.safedialer.window.ComposeFloatingWindow

@Suppress("DEPRECATION", "unused")
class IncomingCallService :
    Service(),
    CallListener,
    CommandReceiver, DIAware {
    override val di by closestDI { mainDI(this) }
    private val notificationManager by instance<NotificationManager>()
    private val notification by instance<Notification>("notification")
    private val incomingCallReceiver by instance<IncomingCallBroadcastReceiver>()
    private val commandsReceiver by instance<ServiceCommandReceiver>()
    private val channel by instance<NotificationChannel>("notificationChannel")
    private val canDrawOverlays: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    private val phoneLookup by instance<PhoneLookup>()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        isStarted = true
        super.onCreate()
        notificationManager.createNotificationChannel(channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL,
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
        // todo ?
//        runCatching {
//            dao.meta.clear()
//            dao.meta.add(MetaData("test", "test"))
//            dao.meta.asList<MetaData>().forEach { o ->
//                Log.d("DAO", o.toString())
//            }
//        }
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

    override fun onBind(intent: Intent?): IBinder? = null

    private fun serviceStop(restart: Boolean) {
        isRestart = restart
        lastAlerts.forEach { la -> la.hide() }
        stopSelf()
    }

    private fun requestOverlayPermission() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
        )
    }

    private fun showAlert(
        caller: String?,
        info: String?,
        isDangerous: Boolean = false,
        context: Context = applicationContext,
    ) = ComposeFloatingWindow(
        context = context,
        windowParams = ComposeFloatingWindow.fullScreenLayoutParams(context),
    ) {
        setContent {
            CallDialog(
                caller = caller,
                info = info,
            )
        }
        show()
        lastAlerts.add(this)
    }

    override fun onIncomingCall(incomingNumber: String?) { // number null
        lastAlerts.hideAll()
        scope.launch {
            phoneLookup.getInfo(incomingNumber).let { info ->
                withContext(Dispatchers.Main) {
                    showAlert(incomingNumber, info.toString())
                }
            }
        }
    }

    override fun onCallEnded(incomingNumber: String?) { // number null
        lastAlerts.hideAll()
    }

    override fun onCallAccepted(incomingNumber: String?) { // number ok
        lastAlerts.hideAll()
    }

    override fun onCommand(
        command: ServiceCommand?,
        data: Bundle?,
    ) {
        when (command) {
            ServiceCommand.ShowAlert -> {
                data?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER).also { phoneNumber ->
                    showAlert(baseContext, phoneNumber)
                }
            }

            ServiceCommand.HideAlert -> {
                lastAlerts.hideAll()
            }

            ServiceCommand.Start -> {
                start(this)
            }

            ServiceCommand.Stop -> {
                serviceStop(false)
            }

            ServiceCommand.Restart -> {
                serviceStop(true)
            }

            else -> {}
        }
    }

    companion object {
        const val CHANNEL_ID = "incoming_call_service_channel"
        private var lastAlerts = mutableListOf<ComposeFloatingWindow>()
        private var isRestart = false
        var isStarted = false

        fun start(context: Context) {
            runCatching {
                SyncManager.ensureAccount(context)
                SyncManager.requestImmediateSync(context)
            }.onFailure { e ->
                e.printStackTrace()
            }
            runCatching {
                Intent(context, IncomingCallService::class.java).let { intent ->
                    ContextCompat.startForegroundService(context, intent)
                }
            }.onFailure { e ->
                e.printStackTrace()
            }
        }

        fun cmd(
            context: Context,
            cmd: ServiceCommand,
            data: Bundle? = null,
        ) {
            context.sendBroadcast(
                Intent(ServiceCommandReceiver.ACTION).apply {
                    putExtra(ServiceCommandReceiver.CMD, cmd.toString())
                    putExtra(ServiceCommandReceiver.DATA, data)
                },
            )
        }

        fun showAlert(
            context: Context,
            phoneNumber: String? = null,
        ) = cmd(
            context,
            ServiceCommand.ShowAlert,
            Bundle().apply {
                putString(TelephonyManager.EXTRA_INCOMING_NUMBER, phoneNumber)
            },
        )

        fun hideAlert(context: Context) = cmd(
            context,
            ServiceCommand.HideAlert,
            null,
        )

        fun stop(context: Context) = cmd(
            context,
            ServiceCommand.Stop,
            null,
        )

        fun restart(context: Context) = cmd(
            context,
            ServiceCommand.Restart,
            null,
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