package org.mjdev.safedialer.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.kodein.di.instance
import org.mjdev.safedialer.providers.custom.email.MailThread
import org.mjdev.safedialer.providers.android.messages.Message
import org.mjdev.safedialer.providers.android.messages.MessageThread
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.extensions.StringExt.extractEmail
import org.mjdev.safedialer.extensions.StringExt.isNotNBlank
import org.mjdev.safedialer.extensions.StringExt.removeWhites
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.calllog.CallsProvider
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.contacts.ContactsProvider
import org.mjdev.safedialer.providers.android.telephony.TelephonyFilter
import org.mjdev.safedialer.providers.android.telephony.TelephonyProvider
import org.mjdev.safedialer.providers.custom.ai.AIItem
import org.mjdev.safedialer.providers.custom.email.EmailsProvider
import org.mjdev.safedialer.providers.custom.email.MailItem
import org.mjdev.safedialer.repository.base.ADataRepository
import org.mjdev.safedialer.repository.base.IDataRepository

class DataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : ADataRepository(context, scope), IDataRepository {

    private val objectCache: MutableMap<Any, Any> = mutableMapOf()

    private val contactsProvider: ContactsProvider by instance()
    private val callsProvider: CallsProvider by instance()
    private val telephonyProvider: TelephonyProvider by instance()
    private val emailsProvider: EmailsProvider by instance()

    override val contacts: Flow<List<Contact>> = providerFlow(contactsProvider) {
        Log.d(TAG, "Fetching contacts from provider...")
        getContacts()?.mergeBy { contact ->
            contact.contactId
        }?.sortedBy { contact ->
            contact.displayName
        } ?: emptyList()
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    private val callsNoContacts: Flow<List<Call>> = providerFlow(callsProvider) {
        getCalls()?.sortedByDescending { call -> call.callDate } ?: emptyList()
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    // todo missing some numbers
    override val calls: Flow<List<Call>> = callsNoContacts.combine(contacts) { calls, contacts ->
        calls.map { call ->
            call.copy(
                contact = contacts.firstOrNull { contact ->
                    contact.phone.contentEquals(call.number) ||
                            contact.normalizedPhone.contentEquals(call.number)
                }
            )
        }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    private val smsThreads: Flow<Map<Long, List<Message>>> = providerFlow(telephonyProvider) {
        getSms(TelephonyFilter.ALL) ?: emptyList()
    }.map { smsList ->
        smsList.map { sms -> Message(sms) }.groupBy { sms -> sms.threadId }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    private val mmsThreads: Flow<Map<Long, List<Message>>> = providerFlow(telephonyProvider) {
        getMms(TelephonyFilter.ALL) ?: emptyList()
    }.map { mmsList ->
        mmsList.map { mms -> Message(mms) }.groupBy { mms -> mms.threadId }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    private val allThreads: Flow<Map<Long, List<Message>>> = smsThreads.combine(
        mmsThreads
    ) { smsMap, mmsMap ->
        (smsMap.keys + mmsMap.keys).associateWith { threadId ->
            (smsMap[threadId].orEmpty() + mmsMap[threadId].orEmpty()).sortedByDescending { it.date }
        }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    // todo some sms or mms are without data, and contact
    override val messageThreads: Flow<List<MessageThread>> =
        allThreads.combine(contacts) { threads, contacts ->
            threads.map { (threadId, msgs) ->
                MessageThread(
                    threadId = threadId,
                    messages = msgs,
                    contact = msgs.firstOrNull { it.address != null }?.address?.let { a ->
                        contacts.firstOrNull { contact ->
                            contact.phone?.contentEquals(a) == true ||
                                    contact.normalizedPhone.contentEquals(a)
                        }
                    }
                )
            }
        }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val emails: Flow<List<MailItem>> = providerFlow(emailsProvider) {
        getEmails() ?: emptyList()
    }.combine(contacts) { emailList, contacts ->
        emailList.filter { m ->
            !m.isDeleted // && !m.isArchived // todo
        }.sortedByDescending { email ->
            email.createdAtMillis
        }.map { email ->
            val senderEmail = email.senderEmail.removeWhites()
//            val senderName = email.senderName.removeWhites()
            val recipient = email.recipients.removeWhites().ifEmpty { senderEmail }
            val contact = contacts.firstOrNull { c ->
                c.email.contentEquals(recipient, true) ||
                        c.emails?.any { e -> e.value.contentEquals(recipient, true) } == true
            }
            email.copy(
                contact = contact
            )
        }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val aiThreads: Flow<List<AIItem>> = flow {
        emit(emptyList<AIItem>()) // todo
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val contactsMap: Flow<Map<String, List<Contact>>> = contacts.map { cl ->
        cl.groupBy { c -> c.displayName?.firstOrNull()?.uppercase() ?: " " }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val callsMap: Flow<Map<String, List<Call>>> = calls.map { cl ->
        cl.groupBy { c -> c.callDate.formatDate() }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val messagesMap = messageThreads.map { threads ->
        threads.groupBy { mt -> mt.date.formatDate() }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    // todo simplify
    override val emailsMap: Flow<Map<String, List<MailThread>>> =
        emails.combine(contacts) { emailList, contacts ->
            emailList.map { mail ->
                MailThread(
                    id = mail.id,
                    contact = mail.contact ?: contacts.firstOrNull { c ->
                        c.email.contentEquals(mail.senderEmail, true) ||
                                c.displayName.contentEquals(mail.senderName, true)
                        c.emails?.any { e -> e.value.contentEquals(mail.senderEmail, true) } == true
                    },
                    messages = listOf(mail)
                )
            }.associate { mt ->
                val created = mt.messages.firstOrNull()?.createdAtMillis ?: 0L
                created.formatDate() to listOf(mt)
            }

//        val participants = mutableSetOf<String>().apply {
//            emailList.forEach { mail ->
//                val sender = mail.senderEmail.trim() // todo sender from sent ?
//                if (sender.isNotEmpty()) this += sender.lowercase()
//                val recipients = mail.recipients
//                    .split(',', ';')
//                    .map { it.trim() }
//                    .filter { it.isNotEmpty() }
//                this += recipients.map { r -> r.lowercase() }
//            }
//        }
//        val result = mutableMapOf<Long, List<MailThread>>().apply {
//            for (participant in participants) {
//                val participantMessages = emailList.filter { mail ->
//                    mail.senderEmail.equals(participant, ignoreCase = true) ||
//                            mail.recipients
//                                .split(',', ';')
//                                .any { it.trim().equals(participant, ignoreCase = true) }
//                }.sortedByDescending { it.createdAtMillis }
//                if (participantMessages.isEmpty()) continue
//                val fromParticipant = participantMessages.firstOrNull {
//                    it.senderEmail.equals(participant, true)
//                }
//                val contact = fromParticipant?.contact
//                val receivedDate = fromParticipant?.createdAtMillis ?: System.currentTimeMillis()
//                this[receivedDate] = listOf(
//                    MailThread(
//                        id = 0L,
//                        contact = contact,
//                        messages = participantMessages,
//                    )
//                )
//            }
//        }
//        result.map { entry -> entry.key.formatDate() to entry.value }.toMap()
        }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override val aiMap = aiThreads.map { threads ->
        threads.groupBy { mt -> mt.createdAtMillis.formatDate() }
    }.flowOn(Dispatchers.Default).shareIn(scope, Eagerly, 1)

    override suspend fun findContactByPhone(
        phoneNumber: String?
    ): Contact? = if (phoneNumber == null) null
    else {
        if (objectCache.contains(phoneNumber)) objectCache[phoneNumber] as? Contact
        else contacts.last().firstOrNull { c ->
            c.phone?.contains(phoneNumber) == true || c.normalizedPhone?.contains(phoneNumber) == true
        }?.apply {
            objectCache[phoneNumber] = this
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    override suspend fun findContactBySender(
        email: String?,
        senderName: String?
    ): Contact? {
        val emailNormalized = email?.extractEmail()
        return if (emailNormalized == null && senderName == null) null
        else {
            if (objectCache.contains(emailNormalized!!)) objectCache[emailNormalized] as? Contact
            else if (objectCache.contains(senderName!!)) objectCache[senderName] as? Contact
            else contacts.last().firstOrNull { c ->
                val isEmail = c.email?.contains(emailNormalized) == true ||
                        c.emails?.any { e ->
                            e.value.contentEquals(emailNormalized, true)
                        } == true
                val isName = c.displayName?.contains(senderName) == true
                isEmail || isName
            }?.apply {
                if (email != null) objectCache[emailNormalized] = this
                if (senderName != null) objectCache[senderName] = this
            }
        }
    }

    companion object {
        // todo some more intelligent solution
        fun <T, K> Iterable<T>.mergeBy(
            selector: (T) -> K
        ): List<T> = distinctBy(selector)
    }
}
