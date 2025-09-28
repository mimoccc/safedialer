package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.Telephony.TextBasedSmsColumns
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.KITKAT)
data class Sms(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = TextBasedSmsColumns.ADDRESS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var address: String? = null,

    @FieldMapping(
        columnName = TextBasedSmsColumns.BODY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var body: String? = null,

    @FieldMapping(
        columnName = TextBasedSmsColumns.DATE,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var receivedDate: Long = 0L,

    @FieldMapping(
        columnName = TextBasedSmsColumns.DATE_SENT,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var sentDate: Long = 0L,

    @FieldMapping(
        columnName = TextBasedSmsColumns.ERROR_CODE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var errorCode: Int = 0,

    @FieldMapping(
        columnName = TextBasedSmsColumns.LOCKED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var locked: Boolean = false,

    @FieldMapping(
        columnName = TextBasedSmsColumns.PERSON,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var person: Int = 0,

    @FieldMapping(
        columnName = TextBasedSmsColumns.PROTOCOL,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var protocol: Int = 0,

    @FieldMapping(
        columnName = TextBasedSmsColumns.READ,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var read: Boolean = false,

    @FieldMapping(
        columnName = TextBasedSmsColumns.SEEN,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var seen: Boolean = false,

    @FieldMapping(
        columnName = TextBasedSmsColumns.SERVICE_CENTER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var serviceCenter: String? = null,

    @FieldMapping(
        columnName = TextBasedSmsColumns.STATUS,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var status: MessageStatus? = null,

    @FieldMapping(
        columnName = TextBasedSmsColumns.SUBJECT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var subject: String? = null,

    @FieldMapping(
        columnName = TextBasedSmsColumns.THREAD_ID,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var threadId: Int = 0,

    @FieldMapping(
        columnName = TextBasedSmsColumns.TYPE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var type: MessageType? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = android.provider.Telephony.Sms.CONTENT_URI

        @IgnoreMapping
        val uriInbox: Uri = android.provider.Telephony.Sms.Inbox.CONTENT_URI

        @IgnoreMapping
        val uriOutbox: Uri = android.provider.Telephony.Sms.Outbox.CONTENT_URI

        @IgnoreMapping
        val uriSent: Uri = android.provider.Telephony.Sms.Sent.CONTENT_URI

        @IgnoreMapping
        val uriDraft: Uri = android.provider.Telephony.Sms.Draft.CONTENT_URI
    }

    enum class MessageType(val value: Int) : EnumInt {
        ALL(TextBasedSmsColumns.MESSAGE_TYPE_ALL),
        INBOX(TextBasedSmsColumns.MESSAGE_TYPE_INBOX),
        SENT(TextBasedSmsColumns.MESSAGE_TYPE_SENT),
        DRAFT(TextBasedSmsColumns.MESSAGE_TYPE_DRAFT),
        OUTBOX(TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX),
        FAILED(TextBasedSmsColumns.MESSAGE_TYPE_FAILED),
        QUEUED(TextBasedSmsColumns.MESSAGE_TYPE_QUEUED);

        companion object {
            fun fromInt(
                value: Int
            ): MessageType? = entries.find { it.value == value }
        }
    }

    enum class MessageStatus(val value: Int) : EnumInt {
        NONE(TextBasedSmsColumns.STATUS_NONE),
        COMPLETE(TextBasedSmsColumns.STATUS_COMPLETE),
        PENDING(TextBasedSmsColumns.STATUS_PENDING),
        FAILED(TextBasedSmsColumns.STATUS_FAILED);

        companion object {
            fun fromInt(
                value: Int
            ): MessageStatus? = entries.find { it.value == value }
        }
    }
}
