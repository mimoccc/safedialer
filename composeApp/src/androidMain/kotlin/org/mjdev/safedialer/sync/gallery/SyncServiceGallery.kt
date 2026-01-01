package org.mjdev.safedialer.sync.gallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.R
import org.mjdev.safedialer.sync.calendar.SyncWorkerCalendar
import org.mjdev.safedialer.sync.calllog.SyncWorkerCallLog
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceGallery : Service() {
    private var adapter: SyncWorkerGallery? = null
    private val dirName = WebDavClient.DIR_GALLERY
    private val providerAuth: String by lazy {
        getString(R.string.authority_gallery)
    }

    override fun onCreate() {
        super.onCreate()
        adapter = SyncWorkerGallery(this, dirName, providerAuth)
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder
}