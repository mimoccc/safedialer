package org.mjdev.safedialer.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import org.mjdev.safedialer.BuildConfig

object SyncManager {
    val ACCOUNT_TYPE = BuildConfig.ACCOUNT_TYPE
    val ACCOUNT_NAME = BuildConfig.SERVER_UNAME

    fun ensureAccount(
        context: Context
    ): Account {
        val am = AccountManager.get(context)
        var account = am.accounts.firstOrNull { a ->
            a.type == ACCOUNT_TYPE && a.name == ACCOUNT_NAME
        }
        if (account == null) {
            account = Account(ACCOUNT_NAME, ACCOUNT_TYPE)
            am.addAccountExplicitly(account, null, null)
            try {
                SyncAccountTypes.entries.forEach { syncAuth ->
                    val authority = context.getString(syncAuth.authority)
                    ContentResolver.setIsSyncable(account, authority, 1)
                    ContentResolver.setSyncAutomatically(account, authority, true)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return account
    }

    fun requestImmediateSync(
        context: Context
    ) {
        val account = ensureAccount(context)
        if (ContentResolver.isSyncActive(account, ContactsContract.AUTHORITY))
            return
        if (ContentResolver.isSyncPending(account, ContactsContract.AUTHORITY))
            return
        val extras = Bundle().apply {
            putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        }
        ContentResolver.requestSync(account, ContactsContract.AUTHORITY, extras)
    }
}
