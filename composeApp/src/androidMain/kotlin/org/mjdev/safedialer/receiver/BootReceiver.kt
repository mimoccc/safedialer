package org.mjdev.safedialer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.mjdev.phone.nsd.service.CallNsdService.Companion.start
import org.mjdev.safedialer.phone.WifiPhoneService
import org.mjdev.safedialer.service.calls.IncomingCallService
import org.mjdev.safedialer.service.media.MediaService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            IncomingCallService.start(context)
            MediaService.start(context)
            context.start<WifiPhoneService>()
        }
    }
}
