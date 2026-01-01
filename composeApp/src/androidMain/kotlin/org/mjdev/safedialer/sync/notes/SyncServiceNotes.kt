package org.mjdev.safedialer.sync.notes

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.saveable.autoSaver
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceNotes : Service() {
    private var adapter: SyncWorkerNotes? = null
    private val dirName = WebDavClient.DIR_NOTES
    private val providerAuth: String by lazy {
        getString(R.string.authority_notes)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerNotes(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}