package org.mjdev.safedialer.data.list

import android.net.Uri
import androidx.core.net.toUri
import org.mjdev.safedialer.helpers.SafeMap
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity

@Suppress("unused")
data class ListItem(
    val entity: Entity? = null,
    val customMapper: (Entity?) -> Map<String, String> = { mapOf() } // todo
) : SafeMap(entity) {
    private val id: Long by this
    private val callId: Long by this
    private val contactId: Long by this

    private val normalizedPhone: String by this
    private val phoneNumber: String by this
    private val phone: String by this

    private val displayName: String by this
    private val name: String by this
    private val senderName: String by this

    private val email: String by this
    private val senderEmail: String by this

    private val date: Long by this
    private val callDate: Long by this

    private val type: CallType? by this
    private val callType: CallType? by this

    private val photoThumbnailUri: Uri by this
    private val photoUri: Uri by this
    private val uriPhoto: Uri by this

    private val duration: Long by this

    val details: String by this

    val isBlocked: Boolean by this
    val isMissed: Boolean by this
    val isIncoming: Boolean by this
    val isOutgoing: Boolean by this
    val isVoicemail: Boolean by this
    val isRejected: Boolean by this
    val isAnswered: Boolean by this
    val isStored: Boolean by this
    val isDanger: Boolean by this

    private val contact: Contact? by this

    // todo may be find it if call?
    val itemId: Long
        get() = callId.ifZero {
            contactId
        }.ifZero {
            contact?.contactId ?: 0L
        }.ifZero {
            callId
        }

    val itemEmail: String
        get() = email.ifEmpty { senderEmail }

    val itemPhone: String
        get() = normalizedPhone.ifEmpty {
            phoneNumber
        }.ifEmpty {
            phone
        }.ifEmpty {
            contact?.normalizedPhone ?: ""
        }.ifEmpty {
            contact?.phone ?: ""
        }.ifEmpty {
            itemEmail
        }

    val itemPhoto: Uri
        get() = uriPhoto.ifEmpty {
            photoThumbnailUri
        }.ifEmpty {
            photoUri
        }.ifEmpty {
            contact?.uriPhoto?.toUri() ?: Uri.EMPTY
        }

    val itemName: String
        get() = name.ifEmpty {
            displayName
        }.ifEmpty {
            contact?.displayName ?: ""
        }.ifEmpty {
            senderName
        }

    val itemDate: Long
        get() = callDate.ifZero { date }

    val itemCallType: CallType?
        get() = type.ifNull { callType }

    companion object {
        val TAG = ListItem::class.simpleName

        fun Uri.ifEmpty(
            block: () -> Uri
        ) = if (this == Uri.EMPTY) block() else this

        fun Long.ifZero(
            block: () -> Long
        ) = if (this == 0L) block() else this

        fun <T> T?.ifNull(
            block: () -> T?
        ) = this ?: block()
    }
}