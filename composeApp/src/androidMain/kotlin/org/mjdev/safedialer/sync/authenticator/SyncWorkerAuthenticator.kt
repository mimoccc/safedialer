package org.mjdev.safedialer.sync.authenticator

import android.content.Context
import android.util.Log
import org.mjdev.safedialer.providers.custom.auth.AuthItem
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import org.mjdev.safedialer.webdav.WebDavClient
import java.nio.file.Path

@Suppress("unused")
class SyncWorkerAuthenticator(
    context: Context
) : SyncWorkerWebDav<AuthItem>(
    context = context,
    dirName = WebDavClient.DIR_AUTHENTICATOR
) {
    override suspend fun prepareLocalFiles() {
        // todo
    }

    override suspend fun mergeConflict(
        conflict: ConflictType,
        pathLocal: Path,
        pathRemote: String,
        localData: ByteArray,
        remoteData: ByteArray
    ): ConflictSolution {
        // todo
        Log.d(TAG, "mergeConflict: ($conflict) $pathLocal -> $pathRemote")
        return when(conflict) {
            ConflictType.MISSING_REMOTE -> {
                ConflictSolution.UPLOAD_LOCAL
            }
            ConflictType.MISSING_LOCAL -> {
                ConflictSolution.DOWNLOAD_REMOTE
            }
            ConflictType.LOCAL_DIFFERENT_FROM_REMOTE -> {
                ConflictSolution.IGNORE
            }
        }
    }

    companion object {
        private val TAG = SyncWorkerAuthenticator::class.simpleName
    }
}
