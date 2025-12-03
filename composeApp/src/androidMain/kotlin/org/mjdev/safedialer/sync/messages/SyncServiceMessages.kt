package org.mjdev.safedialer.sync.messages

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceMessages : Service() {
    private var adapter: SyncWorkerMessages? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerMessages(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}