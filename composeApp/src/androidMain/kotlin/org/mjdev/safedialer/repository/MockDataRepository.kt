package org.mjdev.safedialer.repository

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.mjdev.safedialer.providers.custom.email.MailThread
import org.mjdev.safedialer.providers.android.messages.Message
import org.mjdev.safedialer.providers.android.messages.MessageThread
import org.mjdev.safedialer.extensions.DateExt.formatDate
import org.mjdev.safedialer.extensions.StringExt.removeWhites
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.MmsMessageType
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.android.telephony.SmsMessageType
import org.mjdev.safedialer.providers.custom.ai.AIItem
import org.mjdev.safedialer.providers.custom.email.MailItem
import org.mjdev.safedialer.repository.base.ADataRepository
import org.mjdev.safedialer.repository.base.IDataRepository

class MockDataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
) : ADataRepository(context, scope), IDataRepository {
    override val contacts: Flow<List<Contact>> = flow {
        emit(mockContacts)
    }.flowOn(Dispatchers.Default)

    override val calls: Flow<List<Call>> = contacts.combine(
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
    }.flowOn(Dispatchers.Default)

    private val smsThreads: Flow<Map<Long, List<Sms>>> = flow {
        emit(mockSms)
    }.map { smsList ->
        smsList.groupBy { it.threadId }
    }.flowOn(Dispatchers.Default)

    private val mmsThreads = flow {
        emit(mockMms)
    }.map { mmsList ->
        mmsList.groupBy { it.threadId }
    }.flowOn(Dispatchers.Default)

    override val messageThreads: Flow<List<MessageThread>> = contacts.combine(
        smsThreads.combine(mmsThreads) { smsMap, mmsMap ->
            Pair(smsMap, mmsMap)
        }
    ) { contactsList, pair ->
        val smsMap = pair.first
        val mmsMap = pair.second
        val result = mutableListOf<MessageThread>()
        val allThreadIds = (smsMap.keys.map { it } + mmsMap.keys).toSet()
        for (threadId in allThreadIds) {
            val smsList = smsMap[threadId] ?: emptyList()
            val mmsList = mmsMap[threadId] ?: emptyList()
            val combined = (smsList.map { sms -> Message(sms) } + mmsList.map { mms -> Message(mms) })
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
            result.add(
                MessageThread(
                    threadId = threadId,
                    contact = senderContact,
                    messages = combined,
                )
            )
        }
        result
    }.flowOn(Dispatchers.Default)

    override val emails: Flow<List<MailItem>> = contacts.combine(flow {
        emit(mockEmails)
    }) { lastContacts, emailList ->
        emailList.map { email ->
            email.copy(
                contact = lastContacts.firstOrNull { c ->
                    c.emails?.any { e ->
                        e.value.removeWhites() == email.senderEmail.removeWhites()
                    } ?: false
                }
            )
        }.sortedByDescending { email -> email.createdAtMillis }
    }.flowOn(Dispatchers.Default)

    override val aiThreads: Flow<List<AIItem>> = flow {
        emit(emptyList<AIItem>()) // todo
    }.flowOn(Dispatchers.Default)

    override val callsMap: Flow<Map<String, List<Call>>> = calls.map { cl ->
        cl.groupBy { c ->
            c.callDate.formatDate()
        }
    }.flowOn(Dispatchers.Default)

    override val messagesMap: Flow<Map<String, List<MessageThread>>> = messageThreads.map { threads ->
        threads.groupBy { mt ->
            mt.date.formatDate()
        }
    }.flowOn(Dispatchers.Default)

    override val contactsMap: Flow<Map<String, List<Contact>>> = contacts.map { cl ->
        cl.groupBy { c ->
            c.displayName?.firstOrNull()?.uppercase() ?: ""
        }
    }.flowOn(Dispatchers.Default)

    override val emailsMap: Flow<Map<String, List<MailThread>>> = emails.map { emailList ->
        val participants = mutableSetOf<String>()
        emailList.forEach { mail ->
            val sender = mail.senderEmail.trim()
            if (sender.isNotEmpty()) participants += sender.lowercase()
            val recipients = mail.recipients
                .split(',', ';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            participants += recipients.map { it.lowercase() }
        }
        val result = mutableMapOf<String, List<MailThread>>()
        for (participant in participants) {
            val participantMessages = emailList.filter { mail ->
                mail.senderEmail.equals(participant, ignoreCase = true) ||
                        mail.recipients
                            .split(',', ';')
                            .any { it.trim().equals(participant, ignoreCase = true) }
            }.sortedByDescending { it.createdAtMillis }
            if (participantMessages.isEmpty()) continue
            val fromParticipant =
                participantMessages.firstOrNull { it.senderEmail.equals(participant, true) }
            val contact = fromParticipant?.contact
            val displayName = contact?.displayName?.takeIf { it.isNotBlank() }
                ?: fromParticipant?.senderName?.takeIf { it.isNotBlank() }
                ?: participant
            result[displayName] = listOf(
                MailThread(
                    id = 0L,
                    contact = contact,
                    messages = participantMessages,
                )
            )
        }
        result
    }.flowOn(Dispatchers.Default)

    override val aiMap = aiThreads.map { threads ->
        threads.groupBy { mt -> mt.createdAtMillis.formatDate() }
    }.flowOn(Dispatchers.Default)

    override suspend fun findContactByPhone(
        phoneNumber: String?
    ): Contact? = if (phoneNumber == null) null else contacts.firstOrNull()?.first { c ->
        c.phone?.contains(phoneNumber) == true ||
                c.normalizedPhone?.contains(phoneNumber) == true
    }

    override suspend fun findContactBySender(
        email: String?,
        senderName: String?
    ): Contact? = contacts.firstOrNull()?.first { c ->
        val isEmail = if (email == null) false else {
            c.email?.contains(email) == true ||
                    c.emails?.any { e -> e.value.contentEquals(email) } == true
        }
        val isName = if (senderName == null) false else {
            c.displayName?.contains(senderName) == true
        }
        isEmail || isName
    }

    companion object {
        private val mockContacts = (1..32).map { idx ->
            Contact(
                phone = "+421 999 000 99$idx",
                displayName = "John Doe $idx",
                normalizedPhone = "+421 999 000 99$idx",
                contactId = idx.toLong(),
                id = idx.toLong(),
                email = "john.doe$idx@example.com",
                emails = listOf(),
            )
        }

        private val mockCalls = (1..32).map { idx ->
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

        private val mockSms = (1..32).map { idx ->
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

        private val mockMms = (1..32).map { idx ->
            Mms(
                id = idx.toLong(),
                receivedDate = System.currentTimeMillis(),
                sentDate = System.currentTimeMillis(),
                type = MmsMessageType.INBOX,
                subject = "Hello John Doe $idx",
            )
        }

        private val mockEmails = (1..32).map { idx ->
            MailItem(
                id = idx.toLong(),
                senderName = "John Doe $idx",
                senderEmail = "john.doe$idx@example.com",
                subject = "Hello John Doe $idx",
                createdAtMillis = System.currentTimeMillis(),
                mailboxName = "Inbox",
            )
        }
    }
}
