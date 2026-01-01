package org.mjdev.safedialer.sync.contact

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceContacts : Service() {
    private var adapter: SyncWorkerContacts? = null
    private val dirName = WebDavClient.DIR_CONTACTS
    private val providerAuth: String by lazy {
        getString(R.string.authority_contacts)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerContacts(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}