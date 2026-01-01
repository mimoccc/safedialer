package org.mjdev.safedialer.sync.email

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceEmails : Service() {
    private var adapter: SyncWorkerEmails? = null
    private val dirName = WebDavClient.DIR_IMAP
    private val providerAuth: String by lazy {
        getString(R.string.authority_emails)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerEmails(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}