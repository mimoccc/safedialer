package org.mjdev.safedialer.data.mapper

import org.mjdev.safedialer.providers.custom.email.MailThread
import org.mjdev.safedialer.providers.android.messages.MessageThread
import org.mjdev.safedialer.data.list.ListItem
import org.mjdev.safedialer.providers.android.calllog.Call
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity

object EntityMapper {
    fun <T : Entity> T.asListItem(): ListItem? = when (this) {
        is Call -> CallMapper(this@asListItem as Call)
        is Contact -> ContactMapper(this)
        is MailThread -> EmailThreadMapper(this)
        is MessageThread -> MessageThreadMapper(this)
        else -> null
    }
}