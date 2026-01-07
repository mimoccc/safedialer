package org.mjdev.safedialer.app

import android.app.Application
import androidx.core.telecom.CallsManager
//import kotbase.CouchbaseLite
import org.kodein.di.DIAware
import org.kodein.di.LazyDI
import org.kodein.di.instance
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.service.calls.IncomingCallService
import org.mjdev.safedialer.service.media.MediaService

class MainApp : Application(), DIAware {
    override val di: LazyDI by mainDI(this@MainApp)

    private val callsManager by instance<CallsManager>()
    private val capabilities by instance<Int>("callCapabilities")

    override fun onCreate() {
        callsManager.registerAppWithTelecom(capabilities)
        super.onCreate()
        // todo check if from boot & permissions granted & permission activity
        IncomingCallService.start(this)
        MediaService.start(this)
    }
}
