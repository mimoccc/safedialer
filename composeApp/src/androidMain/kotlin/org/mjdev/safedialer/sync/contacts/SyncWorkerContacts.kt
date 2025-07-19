package org.mjdev.safedialer.sync.contacts

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("unused")
class SyncWorkerContacts(
    context: Context,
) : AbstractThreadedSyncAdapter(context, true) {
//    private val contactsSyncDir = WebDavClient.DIR_CONTACTS,
//    private val vcardFileName = WebDavClient.USER_FILE_VCARD
//
//    private val webDav by lazy {
//        WebDavClient()
//    }
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

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }
}