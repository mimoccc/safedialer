package org.mjdev.safedialer.providers.android.calllog

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.CallLog.Calls
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.KITKAT)
data class Call(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = Calls.CACHED_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var name: String? = null,

    @FieldMapping(
        columnName = Calls.DATE,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var callDate: Long = 0L,

    @FieldMapping(
        columnName = Calls.DURATION,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var duration: Long = 0L,

    @FieldMapping(
        columnName = Calls.IS_READ,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isRead: Boolean = false,

    @FieldMapping(
        columnName = Calls.NUMBER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var number: String? = null,

    @FieldMapping(
        columnName = Calls.TYPE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var type: CallType? = null,

    @IgnoreMapping
    var contact : Contact? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Calls.CONTENT_URI
    }

    enum class CallType(
        val value: Int
    ) : EnumInt {
        INCOMING(Calls.INCOMING_TYPE),
        OUTGOING(Calls.OUTGOING_TYPE),
        BLOCKED(Calls.BLOCKED_TYPE),
        VOICEMAIL(Calls.VOICEMAIL_TYPE),
        REJECTED(Calls.REJECTED_TYPE),
        MISSED(Calls.MISSED_TYPE);

        companion object : CompanionWithUri {
            override val uri: Uri = Calls.CONTENT_URI

            fun fromInt(
                value: Int
            ): CallType? = entries.find { it.value == value }
        }
    }
}
