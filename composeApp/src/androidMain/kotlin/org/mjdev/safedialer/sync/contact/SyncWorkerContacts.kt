package org.mjdev.safedialer.sync.contact

import android.content.Context
import android.util.Log
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import org.mjdev.safedialer.webdav.WebDavClient
import java.nio.file.Path

@Suppress("unused")
class SyncWorkerContacts(
    context: Context
) : SyncWorkerWebDav<Contact>(
    context = context,
    dirName = WebDavClient.DIR_CONTACTS
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
        private val TAG = SyncWorkerContacts::class.simpleName
    }
}

//    private val contactsSyncDir = WebDavClient.DIR_CONTACTS,
//    private val vcardFileName = WebDavClient.USER_FILE_VCARD
//
//    private val webDav by instance()
//
//    private val userVCards by lazy {
//        webDav
//            .readFile(vcardFileName)
//            .toString(Charsets.UTF_8)
//            .let { text ->
//                Ezvcard.parse(text).all()
//            }
//    }
//
//    private val remoteVCards by lazy {
//        webDav.list(contactsSyncDir).filter { fname ->
//            fname.endsWith(".vcf", true)
//        }.flatMap { name ->
//            val fpath = "${contactsSyncDir.trim('/')}/$name"
//            val text = webDav.readFile(fpath).toString(Charsets.UTF_8)
//            Ezvcard.parse(text).all()
//        }
//    }
