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
    SMS(Sms::class),
    MMS(Mms::class),
}

data class Message(
    val type: MessageType = MessageType.SMS,
    val message: Any?,
    val date: Long = 0L
)

data class MessageThread(
    var id: Long = 0,
    val contact: Contact? = null,
    val messages: List<Message> = emptyList(),
    val date: Long = 0L,
    val lastMessage: Message?
) : Entity() {
    val displayName: String?
        get() = contact?.displayName ?: lastMessage?.let { lm ->
            when (lm.type) {
                MessageType.SMS -> (lm.message as Sms).address
                MessageType.MMS -> null// todo mms sender
            }
        }

    companion object : CompanionWithUri {
        override val uri = Uri.EMPTY
    }
}