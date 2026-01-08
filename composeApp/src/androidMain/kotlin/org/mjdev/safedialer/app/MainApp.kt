package org.mjdev.safedialer.app

import android.app.Application
import android.util.Log
import androidx.core.telecom.CallsManager
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
        setupExceptionHandler()
        // todo check if from boot & permissions granted & permission activity
        IncomingCallService.start(this)
        MediaService.start(this)
    }

    private fun setupExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e(TAG, "$TAG_CRASH: Uncaught exception in thread: ${thread.name}", throwable)
            Log.e(TAG, "$TAG_CRASH: Exception: ${throwable.javaClass.name}: ${throwable.message}")
            Log.e(TAG, "$TAG_CRASH: Stack trace:\n${throwable.stackTraceToString()}")
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        private val TAG = MainApp::class.simpleName
        private const val TAG_CRASH = "Crash"
    }
}
