package org.mjdev.safedialer.data.mapper

import android.net.Uri
import androidx.core.net.toUri
import org.mjdev.safedialer.data.custom.MailThread
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.calllog.CallType

class EmailThreadMapper (
    val email: MailThread? = null
) :ListItem {
    override val contact: Contact?
        get() = email?.contact

    override val itemId: Long?
        get() = contact?.contactId ?: contact?.id ?: email?.id
    override val itemEmail: String?
        get() = email?.contact?.email
    override val itemPhone: String?
        get() = email?.lastMessage?.subject ?: email?.contact?.email ?: email?.contact?.phone
    override val itemPhoto: Uri?
        get() = contact?.uriPhoto?.toUri()
    override val itemName: String?
        get() =  contact?.displayName ?: contact?.email ?: email?.lastMessage?.senderName ?: email?.lastMessage?.senderEmail ?: contact?.phone
    override val itemDate: Long?
        get() = email?.date ?: email?.lastMessage?.createdAtMillis
    override val itemCallType: CallType?
        get() = null

    override val details: String
        get() = ""

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
        fun MailThread.asListItem() = EmailThreadMapper(this)
    }
}