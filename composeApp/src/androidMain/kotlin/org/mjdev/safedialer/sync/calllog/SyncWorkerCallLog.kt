package org.mjdev.safedialer.sync.calllog

import android.content.Context
import android.content.SyncResult
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav

class SyncWorkerCallLog(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<Contact>(context, dirName) {
    override fun prepareLocalFiles(
        syncResult: SyncResult?
    ) {
    }

    override fun mergeChanges() {
    }

    companion object {
        private val TAG = SyncWorkerCallLog::class.simpleName
    }
}
