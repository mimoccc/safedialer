package org.mjdev.safedialer.sync.gallery

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.mjdev.safedialer.webdav.WebDavClient

class SyncServiceGallery : Service() {
    private val dirName = WebDavClient.DIR_GALLERY

    override fun onCreate() {
        super.onCreate()
        synchronized(SyncServiceGallery::class.java) {
            if (adapter == null) {
                adapter = SyncWorkerGallery(this, dirName)
            }
        }
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = adapter?.syncAdapterBinder

    companion object {
        private var adapter: SyncWorkerGallery? = null
    }
}