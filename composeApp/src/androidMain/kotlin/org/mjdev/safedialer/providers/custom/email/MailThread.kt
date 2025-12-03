package org.mjdev.safedialer.providers.custom.email

import android.net.Uri
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity

data class MailThread(
    var id: Long = 0,
    val contact: Contact? = null,
    val messages: List<MailItem> = emptyList(),
) : Entity() {
    val displayName: String?
        get() = contact?.displayName ?: lastMessage?.senderName

    val lastMessage: MailItem?
        get() = messages.minByOrNull { it.createdAtMillis }

    val date: Long?
        get() = lastMessage?.createdAtMillis

    companion object : CompanionWithUri {
        override val uri = Uri.EMPTY
    }
}