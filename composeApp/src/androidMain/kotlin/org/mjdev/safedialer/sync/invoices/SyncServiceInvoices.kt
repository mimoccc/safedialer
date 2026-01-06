package org.mjdev.safedialer.sync.invoices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceInvoices : Service() {
    private val dirName = WebDavClient.DIR_INVOICES

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceInvoices::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerInvoices(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerInvoices? = null
    }
}