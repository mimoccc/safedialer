package org.mjdev.safedialer.sync.email

import android.content.Context
import android.content.SyncResult
import android.util.Log
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

class SyncWorkerEmails(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<Contact>(context, dirName) {
    override fun prepareLocalFiles(syncResult: SyncResult?) {
    }

    override fun mergeChanges() {
    }

    override fun mergeConflict(
        conflict: ConflictType,
        pathLocal: Path,
        pathRemote: String,
        localData: ByteArray,
        remoteData: ByteArray
    ): ConflictSolution = runCatching {
        when (conflict) {
            ConflictType.MISSING_REMOTE -> {
                pathLocal.deleteIfExists()
                ConflictSolution.IGNORE
            }

            ConflictType.MISSING_LOCAL -> {
                ConflictSolution.DOWNLOAD_REMOTE
            }

            ConflictType.LOCAL_DIFFERENT_FROM_REMOTE -> {
                ConflictSolution.UPDATE_LOCAL
            }
        }
    }.getOrElse { e ->
        Log.e(TAG, "Error in mergeConflict, using safe fallback", e)
        ConflictSolution.IGNORE
    }

    companion object {
        private val TAG = SyncWorkerEmails::class.simpleName
    }
}
