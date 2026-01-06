package org.mjdev.safedialer.sync.calllog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceCallLog : Service() {
    private val dirName = WebDavClient.DIR_CALL_LOG

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceCallLog::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerCallLog(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerCallLog? = null
    }
}