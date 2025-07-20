package org.mjdev.safedialer.service.calls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER
import android.telephony.TelephonyManager.EXTRA_STATE_IDLE
import android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK
import android.telephony.TelephonyManager.EXTRA_STATE_RINGING
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED

@Suppress("DEPRECATION")
class IncomingCallBroadcastReceiver : BroadcastReceiver() {
    private val listeners = mutableListOf<CallListener>()
    private val filter = IntentFilter(ACTION_PHONE_STATE)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_PHONE_STATE) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(EXTRA_INCOMING_NUMBER)
            if (incomingNumber.isNullOrEmpty().not()) {
                when (state) {
                    EXTRA_STATE_RINGING -> onIncomingCall(incomingNumber)
                    EXTRA_STATE_OFFHOOK -> onCallAccepted(incomingNumber)
                    EXTRA_STATE_IDLE -> onCallEnded(incomingNumber)
                }
            }
        }
    }

    private fun onCallEnded(incomingNumber: String?) {
        listeners.forEach { l -> l.onCallEnded(incomingNumber) }
    }

    private fun onCallAccepted(incomingNumber: String?) {
        listeners.forEach { l -> l.onCallAccepted(incomingNumber) }
    }

    private fun onIncomingCall(incomingNumber: String?) {
        listeners.forEach { l -> l.onIncomingCall(incomingNumber) }
    }

    fun register(context: Context) {
        ContextCompat.registerReceiver(
            context,
            this,
            filter,
            RECEIVER_EXPORTED
        )
        if (context is CallListener) {
            listeners.add(context)
        }
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
        if (context is CallListener) {
            listeners.remove(context)
        }
    }

    companion object {
        val ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE"
    }
}
