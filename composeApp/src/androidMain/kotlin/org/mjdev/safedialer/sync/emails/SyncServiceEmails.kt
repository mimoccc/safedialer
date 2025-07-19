package org.mjdev.safedialer.sync.emails

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceEmails : Service() {
    private var adapter: SyncWorkerEmails? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerEmails(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}