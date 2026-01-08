package org.mjdev.safedialer.sync.task

import android.content.Context
import android.content.SyncResult
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import java.nio.file.Path

class SyncWorkerTasks(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<Contact>(context, dirName) {
    override suspend fun prepareLocalFiles(
        syncResult: SyncResult?
    ) {
    }

    override suspend fun mergeChanges() {
    }

    companion object {
        private val TAG = SyncWorkerTasks::class.simpleName
    }
}
