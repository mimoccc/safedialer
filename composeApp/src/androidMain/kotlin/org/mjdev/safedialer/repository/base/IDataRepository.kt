package org.mjdev.safedialer.repository.base

import kotlinx.coroutines.flow.Flow
import org.mjdev.safedialer.data.custom.MailThread
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.contacts.Contact

interface IDataRepository {
    val contactsMap: Flow<Map<String, List<Contact>>>
    val callsMap: Flow<Map<String, List<Call>>>
    val messagesMap: Flow<Map<String, List<MessageThread>>>
    val emailsMap: Flow<Map<String, List<MailThread>>>

    suspend fun findContactByPhone(phoneNumber: String?): Contact?
    suspend fun findContactBySender(email: String?, senderName: String?): Contact?
}
