package org.mjdev.safedialer.sync.messages

import android.content.Context
import android.content.SyncResult
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import java.nio.file.Path

// todo more items
class SyncWorkerMessages(
    context: Context,
    dirName: String,
    providerAuth: String
) : SyncWorkerWebDav<Contact>(context, dirName, providerAuth) {
    override fun prepareLocalFiles(syncResult: SyncResult?) {
    }

    override fun mergeChanges() {
    }

    companion object {
        private val TAG = SyncWorkerMessages::class.simpleName
    }
}
