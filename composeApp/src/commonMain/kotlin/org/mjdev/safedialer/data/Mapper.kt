package org.mjdev.safedialer.data

import org.mjdev.safedialer.data.enums.CallType.BLOCKED
import org.mjdev.safedialer.data.enums.CallType.INCOMING
import org.mjdev.safedialer.data.enums.CallType.MISSED
import org.mjdev.safedialer.data.enums.CallType.OUTGOING
import org.mjdev.safedialer.data.enums.CallType.REJECTED
import org.mjdev.safedialer.data.enums.CallType.VOICEMAIL
import org.mjdev.safedialer.data.list.IListItem
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.data.model.MessageModel

object Mapper {

    fun IListItem.asListItem() = when (this) {
        is ContactModel -> asListItem()
        is CallModel -> asListItem()
        is MessageModel -> asListItem()
        else -> {
            throw IllegalArgumentException(
                "Unsupported IListItem type: ${this::class.simpleName}"
            )
        }
    }

    fun ContactModel.asListItem() = ListItem(
        callId = null,
        contactId = contactId,
        displayName = displayName,
        phoneNumber = phoneNumber,
        photoThumbnailUri = photoThumbnailUri,
        photoUri = photoUri,
        isBlocked = false, // todo
        details = "", // todo
        isStored = false, // todo do we need?
        isDanger = false // todo
    )

    fun CallModel.asListItem() = ListItem(
        callId = callId,
        phoneNumber = phoneNumber,
        displayName = contact?.displayName ?: phoneNumber,
        contactId = contact?.contactId,
        photoThumbnailUri = contact?.photoThumbnailUri,
        photoUri = contact?.photoUri,
        date = date,
        type = type,
        isBlocked = type == BLOCKED,
        isMissed = type == MISSED,
        isIncoming = type == INCOMING,
        isOutgoing = type == OUTGOING,
        isVoicemail = type == VOICEMAIL,
        isRejected = type == REJECTED,
        isAnswered = type != REJECTED,
        isStored = contact != null,
        isDanger = false // todo
    )

    fun MessageModel.asListItem() = ListItem(
        date = date,
        phoneNumber = phoneNumber,
        displayName = contact?.displayName ?: phoneNumber,
        contactId = contact?.contactId,
    )

}