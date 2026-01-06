package org.mjdev.safedialer.sync.email

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceEmails : Service() {
    private val dirName = WebDavClient.DIR_IMAP

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceEmails::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerEmails(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerEmails? = null
    }
}