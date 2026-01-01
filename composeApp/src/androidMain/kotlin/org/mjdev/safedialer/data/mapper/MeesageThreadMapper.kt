package org.mjdev.safedialer.data.mapper

import android.net.Uri
import androidx.core.net.toUri
import org.mjdev.safedialer.providers.android.messages.MessageThread
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.android.telephony.Mms

class MessageThreadMapper(
    val message: MessageThread? = null
) : ListItem {
    override val contact: Contact?
        get() = message?.contact

    override val itemId: Long?
        get() = message?.contact?.contactId ?: message?.contact?.id ?: message?.threadId
    override val itemEmails: List<String>
        get() = (listOf(contact?.email) + (contact?.emails?.map { e -> e.value } ?: listOf())).filterNotNull()
    override val itemPhone: String?
        get() = message?.contact?.phone ?: message?.lastMessage?.message?.let {
            when (it) {
                is Sms -> it.address
                is Mms -> it.subject
                else -> null
            }
        }
    override val itemPhoto: Uri?
        get() = message?.contact?.uriPhoto?.toUri()
    override val itemName: String?
        get() = message?.contact?.displayName ?: contact?.email
        ?: message?.lastMessage?.message?.let {
            when (it) {
                is Sms -> it.address
                is Mms -> it.subject
                else -> null
            }
        }
    override val itemDate: Long?
        get() = message?.date ?: message?.lastMessage?.date ?: message?.lastMessage?.message?.let {
            when (it) {
                is Sms -> it.receivedDate
                is Mms -> it.receivedDate
                else -> null
            }
        }
    override val itemCallType: CallType?
        get() = null

    override val details: List<String>
        get() = listOf(message?.lastMessage?.body ?: "")

    override val isBlocked: Boolean
        get() = false // todo
    override val isMissed: Boolean
        get() = false // todo
    override val isIncoming: Boolean
        get() = false // todo
    override val isOutgoing: Boolean
        get() = false // todo
    override val isVoicemail: Boolean
        get() = false // todo
    override val isRejected: Boolean
        get() = false // todo
    override val isAnswered: Boolean
        get() = false // todo
    override val isStored: Boolean
        get() = false // todo
    override val isDanger: Boolean
        get() = false // todo

    companion object {
        @Suppress("unused")
        fun MessageThread.asListItem() = MessageThreadMapper(this)
    }
}