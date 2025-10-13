package org.mjdev.safedialer.data.custom

import android.net.Uri
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.core.Entity
import kotlin.reflect.KClass

enum class MessageType(
    val type: KClass<*>
) {
    UNKNOWN(Unit::class),
    SMS(Sms::class),
    MMS(Mms::class);

    companion object {
        operator fun invoke(
            message: Any
        ) = entries.firstOrNull { e -> e.type == message::class }
    }
}

data class Message(
    val message: Any?
) {
    val threadId: Long
        get() = when (message) {
            is Sms -> message.threadId
            is Mms -> message.threadId
            else -> -1
        }

    val address: String?
        get() = when (message) {
            is Sms -> message.address
            is Mms -> message.address
            else -> null
        }

    val type: MessageType
        get() = when (message) {
            is Sms -> MessageType.SMS
            is Mms -> MessageType.MMS
            else -> MessageType.UNKNOWN
        }

    val date: Long
        get() = when (message) {
            is Sms -> message.receivedDate
            is Mms -> message.receivedDate
            else -> -1
        }
}

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