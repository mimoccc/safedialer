package org.mjdev.safedialer.service.command

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.content.ContextCompat

class ServiceCommandReceiver : BroadcastReceiver() {
    private val listeners = mutableListOf<CommandReceiver>()
    private val filter = IntentFilter(ACTION)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val cmd: String = intent.getStringExtra(CMD) ?: ""
            val data: Bundle? = intent.getBundleExtra(DATA)
            onCommand(ServiceCommand(cmd), data)
        }
    }

    private fun onCommand(
        command: ServiceCommand?,
        data: Bundle?
    ) {
        listeners.forEach { listener ->
            listener.onCommand(command, data)
        }
    }

    fun register(context: Context) {
        ContextCompat.registerReceiver(context, this, filter, ContextCompat.RECEIVER_EXPORTED)
        if (context is CommandReceiver) {
            listeners.add(context)
        }
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
        if (context is CommandReceiver) {
            listeners.remove(context)
        }
    }

    companion object {
        val ACTION = ServiceCommandReceiver::class.java.`package`!!.name + ".COMMAND"
        const val CMD = "CMD"
        const val DATA = "DATA"
    }
}
