package org.mjdev.safedialer.repository.base

import kotlinx.coroutines.flow.Flow
import org.mjdev.safedialer.providers.custom.email.MailThread
import org.mjdev.safedialer.providers.android.messages.MessageThread
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.custom.ai.AIItem

interface IDataRepository {
    val contactsMap: Flow<Map<String, List<Contact>>>
    val callsMap: Flow<Map<String, List<Call>>>
    val messagesMap: Flow<Map<String, List<MessageThread>>>
    val emailsMap: Flow<Map<String, List<MailThread>>>
    val aiMap: Flow<Map<String, List<AIItem>>>

    suspend fun findContactByPhone(phoneNumber: String?): Contact?
    suspend fun findContactBySender(email: String?, senderName: String?): Contact?
}
