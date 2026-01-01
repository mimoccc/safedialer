package org.mjdev.safedialer.sync.task

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceTasks : Service() {
    private var adapter: SyncWorkerTasks? = null
    private val dirName = WebDavClient.DIR_TASKS
    private val providerAuth: String by lazy {
        getString(R.string.authority_tasks)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerTasks(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}