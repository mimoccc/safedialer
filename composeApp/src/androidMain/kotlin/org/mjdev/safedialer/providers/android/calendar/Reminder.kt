package org.mjdev.safedialer.providers.android.calendar

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.CalendarContract.Reminders
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
data class Reminder(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = Reminders.EVENT_ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var eventId: Long = 0L,

    @FieldMapping(
        columnName = Reminders.MINUTES,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var minutes: Int = 0,

    @FieldMapping(
        columnName = Reminders.METHOD,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var method: MethodType? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Reminders.CONTENT_URI
    }
}
