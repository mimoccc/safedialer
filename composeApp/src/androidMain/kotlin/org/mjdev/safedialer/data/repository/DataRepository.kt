package org.mjdev.safedialer.data.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import org.kodein.di.instance
import org.mjdev.safedialer.data.custom.Message
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.data.custom.MessageType
import org.mjdev.safedialer.data.repository.base.IDataRepository
import org.mjdev.safedialer.providers.android.calllog.CallsProvider
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.contacts.ContactsProvider
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.android.telephony.TelephonyProvider
import org.mjdev.safedialer.providers.custom.email.EmailsProvider
import org.mjdev.safedialer.providers.custom.email.MailItem
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class DataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : IDataRepository(context, scope) {
    val sdf = SimpleDateFormat("dd.MM.yyyy")

    val contactsProvider: ContactsProvider by instance()
    val callsProvider: CallsProvider by instance()
    val telephonyProvider: TelephonyProvider by instance()
    val emailsProvider: EmailsProvider by instance()

    val contacts = providerObserver(contactsProvider) {
        getContacts()?.filter { pn ->
            pn.displayName.isNotNBlank() && pn.phone.isNotNBlank()
        }?.mergeBy { contact ->
            contact.contactId
        }?.sortedBy { contact ->
            contact.displayName
        } ?: emptyList()
    }.flowOn(Dispatchers.IO).stateIn(scope, Eagerly, emptyList())

    val calls = contacts.combine(
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

    val contactsMap = contacts.map { cl ->
        cl.groupBy { c ->
            c.displayName?.firstOrNull()?.uppercase() ?: ""
        }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    val callsMap = calls.map { cl ->
        cl.groupBy { c ->
            Date(c.callDate).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    val smsThreads = providerObserver(telephonyProvider) {
        getSms(TelephonyProvider.Filter.ALL) ?: emptyList()
    }.map { smsList ->
        smsList.groupBy { it.threadId }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    val mmsThreads = providerObserver(telephonyProvider) {
        getMms(TelephonyProvider.Filter.ALL) ?: emptyList()
    }.map { mmsList ->
        mmsList.groupBy { it.threadId }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    val messageThreads: Flow<Map<Long, List<MessageThread>>> = contacts.combine(
        smsThreads.combine(mmsThreads) { smsMap, mmsMap ->
            Pair(smsMap, mmsMap)
        }
    ) { contactsList, pair ->
        val smsMap = pair.first
        val mmsMap = pair.second
        val result = mutableMapOf<Long, List<MessageThread>>()
        val allThreadIds = (smsMap.keys.map { it.toLong() } + mmsMap.keys).toSet()
        for (threadId in allThreadIds) {
            val smsList = smsMap[threadId.toInt()] ?: emptyList()
            val mmsList = mmsMap[threadId] ?: emptyList()
            val combined = (
                    smsList.map { Message(type = MessageType.SMS, message = it) } +
                            mmsList.map { Message(type = MessageType.MMS, message = it) }
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

    val messagesMap = messageThreads.map { map ->
        map.values.flatten().groupBy { mt ->
            sdf.format(Date(mt.date))
        }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    val emails = contacts.combine(providerObserver(emailsProvider) {
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
    val emailsMap = emails.map { list ->
        list.groupBy { em ->
            sdf.format(Date(em.createdAtMillis))
        }
    }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    fun String?.isNotNBlank(): Boolean =
        this?.isNotBlank() ?: false

    fun String?.removeWhites(): String =
        this?.replace(Regex("\\s+"), "") ?: ""

    // todo some more intelligent solution
    fun <T, K> Iterable<T>.mergeBy(
        selector: (T) -> K
    ): List<T> = distinctBy(selector)

    companion object {
        val TAG: String = DataRepository::class.java.simpleName
    }
}
