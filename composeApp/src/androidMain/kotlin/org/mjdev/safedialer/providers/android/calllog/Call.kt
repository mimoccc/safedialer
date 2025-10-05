package org.mjdev.safedialer.providers.android.calllog

import android.net.Uri
import android.provider.BaseColumns
import android.provider.CallLog.Calls
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping
import org.mjdev.safedialer.providers.core.safeUri

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
    var contact: Contact? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = safeUri {
            Calls.CONTENT_URI
        }
    }
}
