package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.Telephony.BaseMmsColumns
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.KITKAT)
data class Mms(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = BaseMmsColumns.CONTENT_CLASS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var contentClass: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.CONTENT_LOCATION,
        physicalType = FieldMapping.PhysicalType.String
    )
    var contentLocation: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.CONTENT_TYPE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var contentType: String? = null,
    @FieldMapping(
        columnName = BaseMmsColumns.DATE,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var receivedDate: Long = 0L,

    @FieldMapping(
        columnName = BaseMmsColumns.DATE_SENT,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var sentDate: Long = 0L,

    @FieldMapping(
        columnName = BaseMmsColumns.DELIVERY_REPORT,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var deliveryReport: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.EXPIRY,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var expireDate: Long = 0L,

    @FieldMapping(
        columnName = BaseMmsColumns.LOCKED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var locked: Boolean = false,

    @FieldMapping(
        columnName = BaseMmsColumns.MESSAGE_BOX,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var type: MessageType? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.MESSAGE_CLASS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var messageClass: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.MESSAGE_ID,
        physicalType = FieldMapping.PhysicalType.String
    )
    var messageId: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.MESSAGE_SIZE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var messageSize: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.MESSAGE_TYPE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var messageType: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.MMS_VERSION,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var mmsVersion: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.PRIORITY,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var priority: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.READ,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var read: Boolean = false,

    @FieldMapping(
        columnName = BaseMmsColumns.READ_REPORT,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var readReport: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.READ_STATUS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var readStatus: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.REPORT_ALLOWED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var reportAllowed: Boolean = false,

    @FieldMapping(
        columnName = BaseMmsColumns.RESPONSE_STATUS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var responseStatus: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.RESPONSE_TEXT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var responseText: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.RETRIEVE_STATUS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var retrieveStatus: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.RETRIEVE_TEXT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var retrieveText: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.RETRIEVE_TEXT_CHARSET,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var retrieveTextCharset: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.SEEN,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var seen: Boolean = false,

    @FieldMapping(
        columnName = BaseMmsColumns.STATUS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var status: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.SUBJECT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var subject: String? = null,

    @FieldMapping(
        columnName = BaseMmsColumns.SUBJECT_CHARSET,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var subjectCharset: Int = 0,

    @FieldMapping(
        columnName = BaseMmsColumns.TEXT_ONLY,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var textOnly: Boolean = false,

    @FieldMapping(
        columnName = BaseMmsColumns.THREAD_ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var threadId: Long = 0L,

    @FieldMapping(
        columnName = BaseMmsColumns.TRANSACTION_ID,
        physicalType = FieldMapping.PhysicalType.String
    )
    var transactionId: String? = null
) : Entity() {
    companion object  : CompanionWithUri {
        // todo ?
        @IgnoreMapping
        override val uri: Uri = android.provider.Telephony.Mms.CONTENT_URI

        @IgnoreMapping
        val uriInbox: Uri = android.provider.Telephony.Mms.Inbox.CONTENT_URI

        @IgnoreMapping
        val uriOutbox: Uri = android.provider.Telephony.Mms.Outbox.CONTENT_URI

        @IgnoreMapping
        val uriSent: Uri = android.provider.Telephony.Mms.Sent.CONTENT_URI

        @IgnoreMapping
        val uriDraft: Uri = android.provider.Telephony.Mms.Draft.CONTENT_URI
    }

    enum class MessageType(val value: Int) : EnumInt {
        ALL(BaseMmsColumns.MESSAGE_BOX_ALL),
        INBOX(BaseMmsColumns.MESSAGE_BOX_INBOX),
        SENT(BaseMmsColumns.MESSAGE_BOX_SENT),
        DRAFT(BaseMmsColumns.MESSAGE_BOX_DRAFTS),
        OUTBOX(BaseMmsColumns.MESSAGE_BOX_OUTBOX);

        companion object {
            fun fromInt(
                value: Int
            ): MessageType? = entries.find { it.value == value }
        }
    }
}
