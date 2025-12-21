package org.mjdev.safedialer.sync.task

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceTasks : Service() {
    private var adapter: SyncWorkerTasks? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerTasks(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}