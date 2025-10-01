package org.mjdev.safedialer.providers.android.contacts

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
data class Contact(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = CONTACT_ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var contactId: Long = 0L,

    @FieldMapping(
        columnName = Phone.DISPLAY_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var displayName: String? = null,

    @FieldMapping(
        columnName = Phone.NUMBER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var phone: String? = null,

    @FieldMapping(
        columnName = Phone.NORMALIZED_NUMBER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var normalizedPhone: String? = null,

    @FieldMapping(
        columnName = Phone.PHOTO_URI,
        physicalType = FieldMapping.PhysicalType.String
    )
    var uriPhoto: String? = null,

    @FieldMapping(
        columnName = Email.ADDRESS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var emails: List<String?>? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Phone.CONTENT_URI

        @IgnoreMapping
        val uriEmail: Uri = Email.CONTENT_URI
    }
}
