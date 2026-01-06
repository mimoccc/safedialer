package org.mjdev.safedialer.sync.calendar

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceCalendar : Service() {
    private val dirName = WebDavClient.DIR_CALENDAR

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceCalendar::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerCalendar(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerCalendar? = null
    }
}