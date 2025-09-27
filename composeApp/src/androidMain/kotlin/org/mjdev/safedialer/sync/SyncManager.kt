package org.mjdev.safedialer.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import org.mjdev.safedialer.BuildConfig

object SyncManager {
    val TAG = SyncManager::class.simpleName
    val accountType = BuildConfig.ACCOUNT_TYPE
    val accountName = BuildConfig.SERVER_UNAME

    fun ensureAccount(
        context: Context
    ): Account? {
        if (accountType.isNullOrBlank() || accountName.isNullOrBlank()) {
            Log.e(TAG, "AccountType or AccountName is empty. Sync will not work.")
            return null
        } else {
            val am = AccountManager.get(context)
            var account = am.accounts.firstOrNull { a ->
                a.type == accountType && a.name == accountName
            }
            if (account == null) {
                account = Account(accountName, accountType)
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
    }

    fun requestImmediateSync(
        context: Context
    ) {
        val account = ensureAccount(context)
        if (accountType.isNullOrBlank() || accountName.isNullOrBlank()) {
            Log.e(TAG, "AccountType or AccountName is empty. Sync can not work.")
        } else {
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
}
