package org.mjdev.safedialer.sync.messages

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceMessages : Service() {
    private var adapter: SyncWorkerMessages? = null
    private val dirName = WebDavClient.DIR_MESSAGES
    private val providerAuth: String by lazy {
        getString(R.string.authority_messages)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerMessages(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}