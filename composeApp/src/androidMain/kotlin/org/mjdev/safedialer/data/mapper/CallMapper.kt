package org.mjdev.safedialer.data.mapper

import android.net.Uri
import androidx.core.net.toUri
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.android.calllog.CallType

class CallMapper(
    val call: Call? = null
) : ListItem {
    override val contact: Contact?
        get() = call?.contact
    override val itemId: Long?
        get() = call?.contact?.contactId ?: call?.contact?.contactId ?: call?.id
    override val itemEmails: List<String>
        get() = (listOf(contact?.email) + (contact?.emails?.map { e -> e.value } ?: listOf())).filterNotNull()
    override val itemPhone: String?
        get() = call?.contact?.phone ?: call?.number
    override val itemPhoto: Uri?
        get() = call?.contact?.uriPhoto?.toUri()
    override val itemName: String?
        get() = call?.name ?: call?.contact?.displayName ?: contact?.phone ?: call?.number
    override val itemDate: Long?
        get() = call?.callDate
    override val itemCallType: CallType?
        get() = call?.type

    override val details: List<String>
        get() = listOf()
    override val isBlocked: Boolean
        get() = call?.type == CallType.BLOCKED
    override val isMissed: Boolean
        get() = call?.type == CallType.MISSED
    override val isIncoming: Boolean
        get() = call?.type == CallType.INCOMING
    override val isOutgoing: Boolean
        get() = call?.type == CallType.OUTGOING
    override val isVoicemail: Boolean
        get() = call?.type == CallType.VOICEMAIL
    override val isRejected: Boolean
        get() = call?.type == CallType.REJECTED
    override val isAnswered: Boolean
        get() = false // todo
    override val isStored: Boolean
        get() = false // todo
    override val isDanger: Boolean
        get() = false // todo

    companion object {
        @Suppress("unused")
        fun Call.asListItem() = CallMapper(this)
    }
}