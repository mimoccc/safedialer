package org.mjdev.safedialer.sync.calendar

import android.content.Context
import android.content.SyncResult
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import java.nio.file.Path

// custom elements
@Suppress("unused")
class SyncWorkerCalendar(
    context: Context,
    dirName: String,
    providerAuth: String
) : SyncWorkerWebDav<Contact>(context, dirName, providerAuth) {
    override fun prepareLocalFiles(
        syncResult: SyncResult?
    ) {
    }

    override fun mergeChanges() {
    }

    companion object {
        private val TAG = SyncWorkerCalendar::class.simpleName
    }
}
