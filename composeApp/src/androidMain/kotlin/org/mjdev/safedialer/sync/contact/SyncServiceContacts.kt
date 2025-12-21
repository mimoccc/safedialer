package org.mjdev.safedialer.sync.contact

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceContacts : Service() {
    private var adapter: SyncWorkerContacts? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerContacts(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}