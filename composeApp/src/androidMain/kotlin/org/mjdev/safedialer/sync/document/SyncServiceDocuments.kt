package org.mjdev.safedialer.sync.document

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.sync.contact.SyncWorkerContacts
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceDocuments : Service() {
    private var adapter: SyncWorkerDocuments? = null
    private val dirName = WebDavClient.DIR_DOCUMENTS
    private val providerAuth: String by lazy {
        getString(R.string.authority_documents)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerDocuments(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}