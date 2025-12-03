package org.mjdev.safedialer.sync.messages

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle

@Suppress("unused")
class SyncWorkerMessages(
    context: Context,
) : AbstractThreadedSyncAdapter(context, true) {
    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
    }
}