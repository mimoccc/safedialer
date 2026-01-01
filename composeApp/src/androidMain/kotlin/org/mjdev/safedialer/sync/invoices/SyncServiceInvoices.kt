package org.mjdev.safedialer.sync.invoices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceInvoices : Service() {
    private var adapter: SyncWorkerInvoices? = null
    private val dirName = WebDavClient.DIR_INVOICES
    private val providerAuth: String by lazy {
        getString(R.string.authority_invoices)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerInvoices(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}