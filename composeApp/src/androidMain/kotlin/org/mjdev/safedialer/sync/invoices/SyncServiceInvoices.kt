package org.mjdev.safedialer.sync.invoices

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceInvoices : Service() {
    private var adapter: SyncWorkerInvoices? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerInvoices(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}