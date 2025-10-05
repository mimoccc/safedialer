package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.Telephony.Threads
import android.provider.Telephony.ThreadsColumns
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.KITKAT)
data class Thread(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = ThreadsColumns.DATE,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var createdDate: Long = 0L,

    @FieldMapping(
        columnName = ThreadsColumns.READ,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var read: Boolean = false,

    @FieldMapping(
        columnName = ThreadsColumns.TYPE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var type: ThreadType? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Threads.CONTENT_URI
    }


}
