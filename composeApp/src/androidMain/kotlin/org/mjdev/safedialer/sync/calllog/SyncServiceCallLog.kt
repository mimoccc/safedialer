package org.mjdev.safedialer.sync.calllog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.sync.calendar.SyncWorkerCalendar
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceCallLog : Service() {
    private var adapter: SyncWorkerCallLog? = null
    private val dirName = WebDavClient.DIR_CALL_LOG
    private val providerAuth: String by lazy {
        getString(R.string.authority_call_log)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerCallLog(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}