package org.mjdev.safedialer.sync.document

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceDocuments : Service() {
    private var adapter: SyncWorkerDocuments? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerDocuments(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}