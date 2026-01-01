package org.mjdev.safedialer.sync.ai

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceAI : Service() {
    private var adapter: SyncWorkerAI? = null
    private val dirName = WebDavClient.DIR_AI_HISTORY
    private val providerAuth: String by lazy {
        getString(R.string.authority_ai)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerAI(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}