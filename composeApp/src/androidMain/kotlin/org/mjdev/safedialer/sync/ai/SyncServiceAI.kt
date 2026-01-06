package org.mjdev.safedialer.sync.ai

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceAI : Service() {
    private val dirName = WebDavClient.DIR_AI_HISTORY

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceAI::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerAI(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerAI? = null
    }
}