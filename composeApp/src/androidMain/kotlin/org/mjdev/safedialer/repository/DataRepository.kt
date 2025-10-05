package org.mjdev.safedialer.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kodein.di.instance
import org.mjdev.safedialer.data.custom.Message
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.data.custom.MessageType
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.extensions.StringExt.isNotNBlank
import org.mjdev.safedialer.extensions.StringExt.removeWhites
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.calllog.CallsProvider
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.contacts.ContactsProvider
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.android.telephony.TelephonyFilter
import org.mjdev.safedialer.providers.android.telephony.TelephonyProvider
import org.mjdev.safedialer.providers.custom.email.EmailsProvider

class DataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : IDataRepository(context, scope) {
    @Volatile
    private var contactsPreloaded = false

    private val contactsProvider: ContactsProvider by instance()
    private val callsProvider: CallsProvider by instance()
    private val telephonyProvider: TelephonyProvider by instance()
    private val emailsProvider: EmailsProvider by instance()

    override val contacts: Flow<List<Contact>>
        get() = providerObserver(contactsProvider) {
            getContacts()?.filter { pn ->
                pn.displayName.isNotNBlank() && pn.phone.isNotNBlank()
            }?.mergeBy { contact ->
                contact.contactId
            }?.sortedBy { contact ->
                contact.displayName
            } ?: emptyList()
        }.flowOn(Dispatchers.IO).stateIn(scope, Eagerly, emptyList())

    override val contactsMap: Flow<Map<String, List<Contact>>>
        get() = contacts.map { cl ->
            cl.groupBy { c ->
                c.displayName?.firstOrNull()?.uppercase() ?: ""
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val calls: Flow<List<Call>>
        get() = contacts.combine(
            providerObserver(callsProvider) {
                getCalls()?.filter { call ->
                    call.name.isNotNBlank() && call.number.isNotNBlank()
                } ?: emptyList()
            }
        ) { lastContacts, callList ->
            callList.map { call ->
                call.contact = lastContacts.firstOrNull { c ->
                    c.phone.removeWhites() == call.number.removeWhites()
                }
                call
            }.sortedByDescending { call -> call.callDate }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val callsMap: Flow<Map<String, List<Call>>>
        get() = calls.map { cl ->
            cl.groupBy { c ->
                c.callDate.formatDate()
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val smsThreads
        get() = providerObserver(telephonyProvider) {
            getSms(TelephonyFilter.ALL) ?: emptyList()
        }.map { smsList ->
            smsList.groupBy { it.threadId }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val mmsThreads
        get() = providerObserver(telephonyProvider) {
            getMms(TelephonyFilter.ALL) ?: emptyList()
        }.map { mmsList ->
            mmsList.groupBy { it.threadId }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val messageThreads: Flow<Map<Long, List<MessageThread>>>
        get() = contacts.combine(
            smsThreads.combine(mmsThreads) { smsMap, mmsMap ->
                Pair(smsMap, mmsMap)
            }
        ) { contactsList, pair ->
            val smsMap = pair.first
            val mmsMap = pair.second
            val result = mutableMapOf<Long, List<MessageThread>>()
            val allThreadIds = (smsMap.keys.map { it } + mmsMap.keys).toSet()
            for (threadId in allThreadIds) {
                val smsList = smsMap[threadId] ?: emptyList()
                val mmsList = mmsMap[threadId] ?: emptyList()
                val combined = (
                        smsList.map {
                            Message(type = MessageType.SMS, message = it)
                        } + mmsList.map {
                            Message(type = MessageType.MMS, message = it)
                        }
                        )
                val senderContact = when (val firstMsg = combined.firstOrNull()?.message) {
                    is Sms -> contactsList.firstOrNull { contact ->
                        val phone = contact.phone?.removeWhites() ?: ""
                        val normalized = contact.normalizedPhone?.removeWhites() ?: ""
                        val msgAddress = firstMsg.address?.removeWhites() ?: ""
                        phone == msgAddress || normalized == msgAddress
                    }

                    is Mms -> null
                    else -> null
                }
                val date = (combined.maxOfOrNull {
                    when (it.message) {
                        is Sms -> it.message.receivedDate
                        is Mms -> it.message.receivedDate
                        else -> 0L
                    }
                } ?: 0L)
                result[threadId] = listOf(
                    MessageThread(
                        id = threadId,
                        contact = senderContact,
                        messages = combined,
                        date = date,
                        lastMessage = combined.lastOrNull()
                    )
                )
            }
            result
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val messagesMap
        get() = messageThreads.map { map ->
            map.values.flatten().groupBy { mt ->
                mt.date.formatDate()
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val emails
        get() = contacts.combine(providerObserver(emailsProvider) {
            getEmails() ?: emptyList()
        }) { lastContacts, emailList ->
            emailList.map { email ->
                email.copy(
                    contact = lastContacts.firstOrNull { c ->
                        c.emails?.any { e ->
                            e.removeWhites() == email.senderEmail.removeWhites()
                        } ?: false
                    }
                )
            }.sortedByDescending { email -> email.createdAtMillis }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    // todo mail threads
    override val emailsMap
        get() = emails.map { list ->
            list.groupBy { em ->
                em.createdAtMillis.formatDate()
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override fun preloadContacts() {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            if (!contactsPreloaded) {
                contacts.first<List<Contact>?>()
                contactsPreloaded = true
            }
        }
    }

    override suspend fun findContactByPhone(
        phoneNumber: String?
    ): Contact? = if (phoneNumber == null) null
    else contacts.firstOrNull()?.first { c ->
        c.phone?.contains(phoneNumber) == true ||
                c.normalizedPhone?.contains(phoneNumber) == true
    }

    override suspend fun findContactBySender(
        email: String?,
        senderName: String?
    ): Contact? = contacts.firstOrNull()?.first { c ->
        val isEmail = if (email == null) false else {
            c.email?.contains(email) == true ||
                    c.emails?.any { e -> e?.contains(email) == true } == true
        }
        val isName = if (senderName == null) false else {
            c.displayName?.contains(senderName) == true
        }
        isEmail || isName
    }

    companion object {

        // todo some more intelligent solution
        fun <T, K> Iterable<T>.mergeBy(
            selector: (T) -> K
        ): List<T> = distinctBy(selector)

    }
}
