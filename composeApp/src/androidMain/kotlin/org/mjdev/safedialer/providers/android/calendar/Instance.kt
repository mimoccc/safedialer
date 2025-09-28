package org.mjdev.safedialer.providers.android.calendar

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.CalendarContract.Instances
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
data class Instance(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = Instances.EVENT_ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var eventId: Long = 0L,

    @FieldMapping(
        columnName = Instances.BEGIN,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var begin: Long = 0L,

    @FieldMapping(
        columnName = Instances.END,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var end: Long = 0L,

    @FieldMapping(
        columnName = Instances.START_DAY,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var startDay: Int = 0,

    @FieldMapping(
        columnName = Instances.START_MINUTE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var startMinute: Int = 0,

    @FieldMapping(
        columnName = Instances.END_DAY,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var endDay: Int = 0,

    @FieldMapping(
        columnName = Instances.END_MINUTE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var endMinute: Int = 0
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Instances.CONTENT_URI
    }
}
