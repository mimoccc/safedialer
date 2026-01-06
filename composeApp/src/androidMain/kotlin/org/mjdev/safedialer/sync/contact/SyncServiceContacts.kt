package org.mjdev.safedialer.sync.contact

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceContacts : Service() {
    private val dirName = WebDavClient.DIR_CONTACTS

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceContacts::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerContacts(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerContacts? = null
    }
}