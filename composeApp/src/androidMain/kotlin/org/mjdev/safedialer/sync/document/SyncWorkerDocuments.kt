package org.mjdev.safedialer.sync.document

import android.content.Context
import android.content.SyncResult
import org.mjdev.safedialer.providers.custom.document.DocumentItem
import org.mjdev.safedialer.sync.SyncWorkerWebDav

@Suppress("unused")
class SyncWorkerDocuments(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<DocumentItem>(
    context,
    dirName,
) {
    override fun prepareLocalFiles(syncResult: SyncResult?) {
    }

    override fun mergeChanges() {
    }

    companion object {
        private val TAG = SyncWorkerDocuments::class.simpleName
    }
}
