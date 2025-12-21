package org.mjdev.safedialer.sync.gallery

import android.content.Context
import android.os.Environment
import android.util.Log
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import org.mjdev.safedialer.webdav.WebDavClient
import java.nio.file.Path

// todo multiple items
class SyncWorkerGallery(
    context: Context
) : SyncWorkerWebDav<Entity>(
    context = context,
    dirName = WebDavClient.DIR_GALLERY,
    // filesDir = Environment.getExternalStorageDirectory().resolve(WebDavClient.DIR_GALLERY)
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
        private val TAG = SyncWorkerGallery::class.simpleName
    }
}
