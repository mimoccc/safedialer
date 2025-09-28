package org.mjdev.safedialer.providers.android.calendar

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.CalendarContract.Attendees
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
data class Attendee(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = Attendees.EVENT_ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var eventId: Long = 0L,

    @FieldMapping(
        columnName = Attendees.ATTENDEE_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var name: String? = null,

    @FieldMapping(
        columnName = Attendees.ATTENDEE_EMAIL,
        physicalType = FieldMapping.PhysicalType.String
    )
    var email: String? = null,

    @FieldMapping(
        columnName = Attendees.ATTENDEE_RELATIONSHIP,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var relationship: Int = 0,

    @FieldMapping(
        columnName = Attendees.ATTENDEE_TYPE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var type: Int = 0,

    @FieldMapping(
        columnName = Attendees.ATTENDEE_STATUS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var status: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Attendees.CONTENT_URI
    }
}
