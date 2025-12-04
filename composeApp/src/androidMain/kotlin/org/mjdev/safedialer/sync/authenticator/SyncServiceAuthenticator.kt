package org.mjdev.safedialer.sync.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceAuthenticator : Service() {
    private var adapter: SyncWorkerAuthenticator? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerAuthenticator(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}