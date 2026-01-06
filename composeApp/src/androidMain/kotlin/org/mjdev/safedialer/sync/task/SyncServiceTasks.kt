package org.mjdev.safedialer.sync.task

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceTasks : Service() {
    private val dirName = WebDavClient.DIR_TASKS

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceTasks::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerTasks(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerTasks? = null
    }
}