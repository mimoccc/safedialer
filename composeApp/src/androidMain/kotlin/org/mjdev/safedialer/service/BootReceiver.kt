package org.mjdev.safedialer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.mjdev.safedialer.service.calls.IncomingCallService
import org.mjdev.safedialer.service.media.MediaService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            IncomingCallService.start(context)
            MediaService.start(context)
        }
    }
}
