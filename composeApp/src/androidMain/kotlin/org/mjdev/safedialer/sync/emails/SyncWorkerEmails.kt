package org.mjdev.safedialer.sync.emails

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle
import org.kodein.di.DIAware
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import kotlin.getValue

@Suppress("unused")
class SyncWorkerEmails(
    context: Context,
) : AbstractThreadedSyncAdapter(context, true), DIAware {
    override val di by closestDI { mainDI(context) }

//    val mailClient by lazy {
//        MailClient(
//            hostImap = BuildConfig.SERVER,
//            hostSmtp = BuildConfig.SERVER,
//            portImap = BuildConfig.SERVER_PORT_IMAP.toInt(),
//            portSmtp = BuildConfig.SERVER_PORT_SMTP.toInt(),
//            userImap = BuildConfig.SERVER_UNAME,
//            passwordImap = BuildConfig.SERVER_UPASS,
//            userSmtp = BuildConfig.SERVER_UNAME,
//            passwordSmtp = BuildConfig.SERVER_UPASS,
//            props = Properties(),
//        )
//    }

//    val dao: DAO by instance<DAO>()

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            mailClient.allMails.collectLatest { emails ->
//                emails.forEach { email ->
//                    dao.emails.add(email)
//                }
//            }
//        }
    }
}
