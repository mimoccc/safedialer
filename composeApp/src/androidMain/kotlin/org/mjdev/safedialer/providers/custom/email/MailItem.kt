package org.mjdev.safedialer.providers.custom.email

import android.net.Uri
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.IgnoreMapping
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.sync.emails.ProviderEmails

data class MailItem(
    @FieldMapping(
        ProviderEmails.MAIL_ITEM_ID,
        FieldMapping.PhysicalType.Long
    )
    val id: Long = 0L,

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_SENDER_NAME,
        FieldMapping.PhysicalType.String
    )
    val senderName: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_SENDER_EMAIL,
        FieldMapping.PhysicalType.String
    )
    val senderEmail: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_SUBJECT,
        FieldMapping.PhysicalType.String
    )
    val subject: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_BODY,
        FieldMapping.PhysicalType.String
    )
    val body: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_CREATED_AT_MILLIS,
        FieldMapping.PhysicalType.Long
    )
    val createdAtMillis: Long = 0L,

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_MAILBOX_NAME,
        FieldMapping.PhysicalType.String
    )
    val mailboxName: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_RECIPIENTS_CSV,
        FieldMapping.PhysicalType.String
    )
    val recipients: String = "",

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_IS_DELETED,
        FieldMapping.PhysicalType.Int,
        FieldMapping.LogicalType.Boolean
    )
    val isDeleted: Boolean = false,

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_IS_FLAGGED,
        FieldMapping.PhysicalType.Int,
        FieldMapping.LogicalType.Boolean
    )
    val isFlagged: Boolean = false,

    @FieldMapping(
        ProviderEmails.MAIL_ITEM_IS_ENCRYPTED,
        FieldMapping.PhysicalType.Int,
        FieldMapping.LogicalType.Boolean
    )
    val isEncrypted: Boolean = false,

    @IgnoreMapping
    val contact: Contact? = null,
) : Entity() {

    // todo : mail folders
    val isArchived
        get() = mailboxName.contentEquals("Archives", true)

    companion object : CompanionWithUri {
        override val uri: Uri = Uri.EMPTY
    }
}
