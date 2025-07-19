package org.mjdev.safedialer.sync.calllog

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.sync.calendar.SyncWorkerCalendar

class SyncServiceCallLog : Service() {
    private var adapter: SyncWorkerCallLog? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerCallLog(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}