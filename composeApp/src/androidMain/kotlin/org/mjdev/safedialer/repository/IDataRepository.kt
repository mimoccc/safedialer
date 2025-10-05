package org.mjdev.safedialer.repository

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.di.mainDI
import org.mjdev.safedialer.extensions.CustomExt.closestDI
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.custom.email.MailItem
import kotlin.reflect.full.companionObjectInstance

abstract class IDataRepository(
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : DIAware {
    override val di: DI by context.closestDI { mainDI(context) }

    abstract val contacts: Flow<List<Contact>>
    abstract val contactsMap: Flow<Map<String, List<Contact>>>

    abstract val calls: Flow<List<Call>>
    abstract val callsMap: Flow<Map<String, List<Call>>>

    abstract val smsThreads: Flow<Map<Long, List<Sms>>>
    abstract val mmsThreads: Flow<Map<Long, List<Mms>>>
    abstract val messageThreads: Flow<Map<Long, List<MessageThread>>>
    abstract val messagesMap: Flow<Map<String, List<MessageThread>>>

    abstract val emails: Flow<List<MailItem>>
    abstract val emailsMap: Flow<Map<String, List<MailItem>>>

    abstract fun preloadContacts()

    abstract suspend fun findContactByPhone(phoneNumber: String?): Contact?
    abstract suspend fun findContactBySender(email: String?, senderName: String?): Contact?

    inline fun <reified E : AbstractProvider, reified T : Entity> providerObserver(
        provider: E,
        crossinline block: suspend E.() -> List<T>
    ) = callbackFlow {
        val companion = T::class.companionObjectInstance as? Entity.CompanionWithUri
            ?: error("Entity object must implement CompanionWithUri")
        val uri: Uri = companion.uri
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                scope.launch {
                    val entities = runCatching {
                        block(provider)
                    }.getOrElse { exception ->
                        exception.printStackTrace()
                        emptyList()
                    }
                    trySend(entities)
                }
            }
        }
        runCatching {
            if (uri != Uri.EMPTY) {
                provider.registerContentObserver(uri, observer)
            } else {
                Log.e(TAG, "Got empty uri. No observer registered.")
            }
        }
        observer.onChange(false)
        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    companion object {
        val TAG = IDataRepository::class.java.simpleName
    }
}
