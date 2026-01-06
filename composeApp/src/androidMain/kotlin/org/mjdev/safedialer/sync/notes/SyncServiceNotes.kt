package org.mjdev.safedialer.sync.notes

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceNotes : Service() {
    private val dirName = WebDavClient.DIR_NOTES

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceNotes::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerNotes(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerNotes? = null
    }
}