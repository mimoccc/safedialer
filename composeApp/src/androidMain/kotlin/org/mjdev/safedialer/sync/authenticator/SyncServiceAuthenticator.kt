package org.mjdev.safedialer.sync.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceAuthenticator : Service() {
    private val dirName = WebDavClient.DIR_AUTHENTICATOR

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceAuthenticator::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerAuthenticator(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerAuthenticator? = null
    }
}