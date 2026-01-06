package org.mjdev.safedialer.sync.document

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceDocuments : Service() {
    private val dirName = WebDavClient.DIR_DOCUMENTS

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceDocuments::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerDocuments(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerDocuments? = null
    }
}