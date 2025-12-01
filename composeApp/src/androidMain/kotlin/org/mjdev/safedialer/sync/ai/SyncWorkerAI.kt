package org.mjdev.safedialer.sync.ai

import android.accounts.Account
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.Context
import android.content.SyncResult
import android.os.Bundle

@Suppress("unused")
class SyncWorkerAI(
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