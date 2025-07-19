package org.mjdev.safedialer.sync.gallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.sync.calendar.SyncWorkerCalendar
import org.mjdev.safedialer.sync.calllog.SyncWorkerCallLog

class SyncServiceGallery : Service() {
    private var adapter: SyncWorkerGallery? = null

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerGallery(this)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}