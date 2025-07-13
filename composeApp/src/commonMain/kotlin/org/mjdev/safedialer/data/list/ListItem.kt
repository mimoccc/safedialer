package org.mjdev.safedialer.data.list

import org.mjdev.safedialer.data.enums.CallType

data class ListItem (
    override val phoneNumber: String,
    override val displayName: String,
    override val date: Long = 0L,
    val callId: String? = null,
    val contactId: String? = null,
    val type: CallType? = null,
    val photoThumbnailUri: String? = null,
    val photoUri: String? = null,
    val details: Any? = null,
    val isBlocked: Boolean = false,
    val isMissed : Boolean = false,
    val isIncoming : Boolean = false,
    val isOutgoing : Boolean = false,
    val isVoicemail : Boolean = false,
    val isRejected: Boolean = false,
    val isAnswered: Boolean = false,
    val isStored: Boolean = false,
    val isDanger: Boolean = false,
) : IListItem