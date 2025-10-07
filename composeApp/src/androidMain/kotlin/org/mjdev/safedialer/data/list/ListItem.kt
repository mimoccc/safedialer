package org.mjdev.safedialer.data.list

import android.net.Uri
import org.mjdev.safedialer.data.custom.MailThread
import org.mjdev.safedialer.data.custom.MessageThread
import org.mjdev.safedialer.data.mapper.CallMapper
import org.mjdev.safedialer.data.mapper.ContactMapper
import org.mjdev.safedialer.data.mapper.EmailThreadMapper
import org.mjdev.safedialer.data.mapper.MessageThreadMapper
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.calllog.CallType
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity

@Suppress("unused")
interface ListItem {
    val contact: Contact?

    val itemId: Long?
    val itemEmail: String?
    val itemPhone: String?
    val itemPhoto: Uri?
    val itemName: String?
    val itemDate: Long?
    val itemCallType: CallType?

    val details: String?

    val isBlocked: Boolean
    val isMissed: Boolean
    val isIncoming: Boolean
    val isOutgoing: Boolean
    val isVoicemail: Boolean
    val isRejected: Boolean
    val isAnswered: Boolean
    val isStored: Boolean
    val isDanger: Boolean

    companion object {
        val TAG = ListItem::class.simpleName

        val PREVIEW = object : ListItem {
            override val details: String
                get() = ""
            override val isBlocked: Boolean
                get() = false
            override val isMissed: Boolean
                get() = false
            override val isIncoming: Boolean
                get() = false
            override val isOutgoing: Boolean
                get() = false
            override val isVoicemail: Boolean
                get() = false
            override val isRejected: Boolean
                get() = false
            override val isAnswered: Boolean
                get() = false
            override val isStored: Boolean
                get() = false
            override val isDanger: Boolean
                get() = false
            override val contact: Contact?
                get() = null
            override val itemId: Long
                get() = 0L
            override val itemEmail: String
                get() = "john.doe@test.com"
            override val itemPhone: String
                get() = "+420777333444555"
            override val itemPhoto: Uri
                get() = Uri.EMPTY
            override val itemName: String
                get() = "John Doe"
            override val itemDate: Long
                get() = System.currentTimeMillis()
            override val itemCallType: CallType?
                get() = null
        }
    }
}