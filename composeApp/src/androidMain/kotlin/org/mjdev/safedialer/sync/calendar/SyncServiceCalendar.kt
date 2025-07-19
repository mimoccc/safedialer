package org.mjdev.safedialer.sync.calendar

import android.app.Service
import android.content.Intent
import android.os.IBinder

class SyncServiceCalendar : Service() {
    private var adapter: SyncWorkerCalendar? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerCalendar(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}