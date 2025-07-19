package org.mjdev.safedialer.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncAuthenticatorService : Service() {
    private var authenticator: SyncAuthenticator? = null

    override fun onCreate() {
        super.onCreate()
        authenticator = SyncAuthenticator(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = authenticator?.iBinder
}
