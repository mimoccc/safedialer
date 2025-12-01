package org.mjdev.safedialer.sync.ai

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceAI : Service() {
    private var adapter: SyncWorkerAI? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerAI(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}