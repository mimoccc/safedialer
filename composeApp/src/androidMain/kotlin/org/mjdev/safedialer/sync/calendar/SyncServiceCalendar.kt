package org.mjdev.safedialer.sync.calendar

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceCalendar : Service() {
    private var adapter: SyncWorkerCalendar? = null
    private val dirName = WebDavClient.DIR_CALENDAR
    private val providerAuth: String by lazy {
        getString(R.string.authority_calendar)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerCalendar(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}