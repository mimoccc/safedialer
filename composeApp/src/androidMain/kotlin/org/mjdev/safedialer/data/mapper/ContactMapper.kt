package org.mjdev.safedialer.data.mapper

import android.net.Uri
import androidx.core.net.toUri
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.calllog.CallType

class ContactMapper (
    override val contact: Contact? = null
) :ListItem {
//    override val contact: Contact?
//        get() = null
    override val itemId: Long?
        get() = contact?.contactId ?: contact?.id
    override val itemEmail: String?
        get() = contact?.email ?: contact?.emails?.firstOrNull()
    override val itemPhone: String?
        get() = contact?.phone
    override val itemPhoto: Uri?
        get() = contact?.uriPhoto?.toUri()
    override val itemName: String?
        get() =  contact?.displayName ?: contact?.phone
    override val itemDate: Long?
        get() = null
    override val itemCallType: CallType?
        get() = null

    override val details: String?
        get() = null

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
        fun Contact.asListItem() = ContactMapper(this)
    }
}