package org.mjdev.safedialer.app

import android.app.Application
import android.content.Context
import androidx.core.telecom.CallsManager
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.mjdev.safedialer.di.appModule
import org.mjdev.safedialer.di.permissionsModule
import org.mjdev.safedialer.di.viewModelsModule
import org.mjdev.safedialer.service.IncomingCallService

class MainApp : Application(), DIAware {
    override val di = DI.lazy {
        bindSingleton<Context> { this@MainApp }
        bindSingleton<MainApp> { this@MainApp }
        import(appModule)
        import(viewModelsModule)
        import(permissionsModule)
    }

    val callsManager by instance<CallsManager>()
    val capabilities by instance<Int>("callCapabilities")

    override fun onCreate() {
        callsManager.registerAppWithTelecom(capabilities)
        super.onCreate()
        // todo check if from boot & permissions granted & permission activity
        IncomingCallService.start(this)
    }
}
