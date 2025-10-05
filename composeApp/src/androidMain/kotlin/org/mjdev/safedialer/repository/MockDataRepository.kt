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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mjdev.safedialer.data.custom.Message
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.data.custom.MessageType
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.extensions.StringExt.removeWhites
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.MmsMessageType
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.android.telephony.SmsMessageType
import org.mjdev.safedialer.providers.custom.email.MailItem

class MockDataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : IDataRepository(context, scope) {

    @Volatile
    private var contactsPreloaded = false

    override val contacts: Flow<List<Contact>>
        get() = flow {
            emit(mockContacts)
        }.flowOn(Dispatchers.IO).stateIn(scope, Eagerly, emptyList())

    override val contactsMap: Flow<Map<String, List<Contact>>>
        get() = contacts.map { cl ->
            cl.groupBy { c ->
                c.displayName?.firstOrNull()?.uppercase() ?: ""
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val calls: Flow<List<Call>>
        get() = contacts.combine(
            flow {
                emit(mockCalls)
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

    override val smsThreads: Flow<Map<Long, List<Sms>>>
        get() = flow {
            emit(mockSms)
        }.map { smsList ->
            smsList.groupBy { it.threadId }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val mmsThreads
        get() = flow {
            emit(mockMms)
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
                val combined = (smsList.map {
                    Message(type = MessageType.SMS, message = it)
                } + mmsList.map {
                    Message(type = MessageType.MMS, message = it)
                })
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
                        is Sms -> (it.message as? Sms)?.receivedDate ?: 0L
                        is Mms -> (it.message as? Mms)?.receivedDate ?: 0L
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

    override val messagesMap: Flow<Map<String, List<MessageThread>>>
        get() = messageThreads.map { map ->
            map.values.flatten().groupBy { mt ->
                mt.date.formatDate()
            }
        }.flowOn(Dispatchers.IO).shareIn(scope, Eagerly, 1)

    override val emails: Flow<List<MailItem>>
        get() = contacts.combine(flow {
            emit(mockEmails)
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
    override val emailsMap: Flow<Map<String, List<MailItem>>>
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

        val mockContacts = (1..32).map { idx ->
            Contact(
                phone = "+421 999 000 99$idx",
                displayName = "John Doe $idx",
                normalizedPhone = "+421 999 000 99$idx",
                contactId = idx.toLong(),
                id = idx.toLong(),
                email = "john.doe$idx@example.com",
                emails = listOf("john.doe$idx@example.com"),
            )
        }

        val mockCalls = (1..32).map { idx ->
            Call(
                id = idx.toLong(),
                name = "John Doe $idx",
                callDate = System.currentTimeMillis(),
                duration = 1000,
                isRead = false,
                number = "+421 999 000 99$idx",
                type = CallType.INCOMING,
                contact = mockContacts.firstOrNull { fc -> fc.phone == "+421 999 000 99$idx" },
            )
        }

        val mockSms = (1..32).map { idx ->
            Sms(
                id = idx.toLong(),
                receivedDate = System.currentTimeMillis(),
                sentDate = System.currentTimeMillis(),
                address = "+421 999 000 99$idx",
                type = SmsMessageType.INBOX,
                subject = "Hello John Doe $idx",
                body = "Hello John Doe $idx"
            )
        }

        val mockMms = (1..32).map { idx ->
            Mms(
                id = idx.toLong(),
                receivedDate = System.currentTimeMillis(),
                sentDate = System.currentTimeMillis(),
                type = MmsMessageType.INBOX,
                subject = "Hello John Doe $idx",
            )
        }

        val mockEmails = (1..32).map { idx ->
            MailItem(
                id = idx.toLong(),
                senderName = "John Doe $idx",
                subject = "Hello John Doe $idx",
                senderEmail = "john.doe$idx@example.com",
                body = "Hello John Doe $idx",
                createdAtMillis = System.currentTimeMillis(),
                mailboxName = "Inbox"
            )
        }
    }
}
