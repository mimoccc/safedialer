package org.mjdev.safedialer.providers.android.messages

import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms

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