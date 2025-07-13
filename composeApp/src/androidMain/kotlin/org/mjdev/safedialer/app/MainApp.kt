package org.mjdev.safedialer.app

import android.app.Application
import androidx.core.telecom.CallsManager
import org.mjdev.safedialer.service.IncomingCallService

class MainApp : Application() {
    val callsManager by lazy {
        CallsManager(this)
    }

    val capabilities: Int
        get() = (CallsManager.CAPABILITY_BASELINE or CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING)

    override fun onCreate() {
        callsManager.registerAppWithTelecom(capabilities)
        super.onCreate()
        // todo check if from boot & permissions granted & permission activity
        IncomingCallService.start(this)
    }
}
