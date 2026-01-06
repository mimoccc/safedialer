package org.mjdev.safedialer.sync.messages

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceMessages : Service() {
    private val dirName = WebDavClient.DIR_MESSAGES

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceMessages::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerMessages(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerMessages? = null
    }
}