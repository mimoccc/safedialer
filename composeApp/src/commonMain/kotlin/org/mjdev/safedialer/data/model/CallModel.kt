package org.mjdev.safedialer.data.model

import org.mjdev.safedialer.data.enums.CallType
import org.mjdev.safedialer.data.list.IListItem

data class CallModel(
    override val phoneNumber: String = "+420 702 568 909",
    override val date: Long = 0L,
    val callId: String? = null,
    val contactId: String? = null,
    val duration: Long? = null,
    val type: CallType? = null,
    val contact: ContactModel? = null,
    val details: Any? = null,
) : IListItem {
    override val displayName: String
        get() = contact?.displayName ?: phoneNumber
}