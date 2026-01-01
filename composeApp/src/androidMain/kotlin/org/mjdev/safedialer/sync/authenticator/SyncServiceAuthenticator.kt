package org.mjdev.safedialer.sync.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceAuthenticator : Service() {
    private var adapter: SyncWorkerAuthenticator? = null
    private val dirName = WebDavClient.DIR_AUTHENTICATOR
    private val providerAuth: String by lazy {
        getString(R.string.authority_authenticator)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerAuthenticator(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}