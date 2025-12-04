package org.mjdev.safedialer.sync.notes

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceNotes : Service() {
    private var adapter: SyncWorkerNotes? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerNotes(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}