package org.mjdev.safedialer.sync.document

import android.content.Context
import android.content.SyncResult
import android.os.Environment
import org.mjdev.safedialer.providers.custom.document.DocumentItem
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import java.nio.file.Path

@Suppress("unused")
class SyncWorkerDocuments(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<DocumentItem>(
    context,
    dirName,
) {
    override suspend fun prepareLocalFiles(
        syncResult: SyncResult?
    ) {
    }

    override suspend fun mergeChanges() {
    }

    override fun overrideBasePath(path: Path): Path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOCUMENTS
    ).toPath()

    companion object {
        private val TAG = SyncWorkerDocuments::class.simpleName
    }
}
