package org.mjdev.safedialer.providers.android.messages

import android.net.Uri
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.core.Entity

data class MessageThread(
    var threadId: Long = 0,
    val contact: Contact? = null,
    val messages: List<Message> = emptyList()
) : Entity() {
    val displayName: String?
        get() = contact?.displayName ?: lastMessage?.let { lm ->
            when (lm.type) {
                MessageType.SMS -> (lm.message as Sms).address?: lm.message.subject
                MessageType.MMS -> (lm.message as Mms).address ?: lm.message.subject
                else -> lm.message.toString()
            }
        }

    val lastMessage: Message?
        get() = messages.maxByOrNull { m -> m.date }

    val date: Long?
        get() = lastMessage?.date

    companion object : CompanionWithUri {
        override val uri = Uri.EMPTY
    }
}