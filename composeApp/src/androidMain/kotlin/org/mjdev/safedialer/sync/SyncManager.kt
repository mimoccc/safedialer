package org.mjdev.safedialer.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.ContentResolver.getIsSyncable
import android.content.ContentResolver.getSyncAutomatically
import android.content.ContentResolver.isSyncActive
import android.content.ContentResolver.isSyncPending
import android.content.ContentResolver.setIsSyncable
import android.content.ContentResolver.setSyncAutomatically
import android.content.ContentResolver.requestSync
import android.content.ContentResolver.cancelSync
import android.content.Context
import android.os.Bundle
import android.util.Log
import org.mjdev.safedialer.BuildConfig

@Suppress("DEPRECATION")
object SyncManager {
    private val TAG = SyncManager::class.simpleName

    private val accountType = BuildConfig.ACCOUNT_TYPE
    private val accountName = BuildConfig.SERVER_UNAME
    private val accountPwd = BuildConfig.SERVER_UPASS

    fun ensureAccount(
        context: Context
    ): Account? {
        return if (accountType.isBlank() || accountName.isBlank()) {
            Log.e(TAG, "AccountType or AccountName is empty. Sync will not work.")
            null
        } else {
            val am = AccountManager.get(context)
            var account = am.accounts.firstOrNull { a ->
                a.type == accountType && a.name == accountName
            }
            if (account == null) {
                account = Account(accountName, accountType)
                runCatching {
                    am.addAccountExplicitly(
                        account,
                        accountPwd,
                        Bundle().apply {
                            putString(ContentResolver.SYNC_EXTRAS_ACCOUNT, accountName)
                        }
                    )
                    runCatching {
                        ContentResolver.getSyncAdapterTypes().filter { syncAdapter ->
                            syncAdapter.accountType == accountType
                        }.forEach { syncAdapterType ->
                            setIsSyncable(account, syncAdapterType.authority, 1)
                            setSyncAutomatically(account, syncAdapterType.authority, true)
                        }
                    }.onFailure { e ->
                        e.printStackTrace()
                    }
                }.onFailure { e ->
                    e.printStackTrace()
                }
            }
            account
        }
    }

    fun requestImmediateSync(
        context: Context,
        authority: String = ""
    ) {
        Log.d(TAG, "Requesting account sync.")
        val account = ensureAccount(context)
        if (accountType.isBlank() || accountName.isBlank()) {
            Log.e(TAG, "AccountType or AccountName is empty. Sync can not work.")
        } else {
            val syncBundle = Bundle().apply {
                putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true)
                putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true)
                putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true)
            }
            ContentResolver.getSyncAdapterTypes().filter { syncAdapter ->
                syncAdapter.accountType == accountType
            }.forEach { syncAdapterType ->
                if (authority.isEmpty() || syncAdapterType.authority == authority) {
                    val syncForText = "sync for: ${syncAdapterType.authority}"
                    Log.d(TAG, "Checking $syncForText")
                    val isRunning = isSyncActive(account, syncAdapterType.authority)
                    val isPending = isSyncPending(account, syncAdapterType.authority)
                    val isEnabled = getSyncAutomatically(account, syncAdapterType.authority)
                    val isSyncable: Boolean = getIsSyncable(account, syncAdapterType.authority) == 1
                    if (isEnabled) {
                        if (isRunning) {
                            Log.d(TAG, "Sync $syncForText, already active.")
                            cancelSync(account, syncAdapterType.authority)
                        }
                        if (isPending) {
                            Log.d(TAG, "Sync $syncForText, already active, but pending.")
                            cancelSync(account, syncAdapterType.authority)
                        }
                        if (!isSyncable) {
                            setIsSyncable(account, syncAdapterType.authority, 1)
                        }
                        Log.d(TAG, "Sync for: ${syncAdapterType.authority}, requested.")
                        requestSync(account, syncAdapterType.authority, syncBundle)
                    }
                }
            }
        }
    }
}
