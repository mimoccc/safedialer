package org.mjdev.safedialer.providers.android.contacts

import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract.CommonDataKinds.Email

import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID

import android.provider.ContactsContract.Contacts

import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping
import org.mjdev.safedialer.providers.core.LabeledValue

@Suppress("DEPRECATION", "unused")
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
        columnName = Phone.PHOTO_THUMBNAIL_URI,
        physicalType = FieldMapping.PhysicalType.String
    )
    var uriThumbnail: String? = null,

    @FieldMapping(
        columnName = Email.ADDRESS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var email: String? = null,

    @FieldMapping(
        columnName = Phone.HAS_PHONE_NUMBER,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var hasPhoneNumber: Boolean = false,

    @FieldMapping(
        columnName = Contacts.STARRED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var starred: Boolean = false,

    @FieldMapping(
        columnName = Contacts.LAST_TIME_CONTACTED,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var lastTimeContacted: Long = 0L,

    @FieldMapping(
        columnName = Contacts.TIMES_CONTACTED,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var timesContacted: Int = 0,

    @FieldMapping(
        columnName = Contacts.LOOKUP_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var lookupKey: String? = null,

    @FieldMapping(
        columnName = Contacts.CUSTOM_RINGTONE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var customRingtone: String? = null,

    @FieldMapping(
        columnName = Contacts.SEND_TO_VOICEMAIL,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var sendToVoicemail: Boolean = false,

    @FieldMapping(
        columnName = Contacts.IN_VISIBLE_GROUP,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var inVisibleGroup: Boolean = false,

    @FieldMapping(
        columnName = Contacts.PHONETIC_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var phoneticName: String? = null,

    @IgnoreMapping
    var namePrefix: String? = null,

    @IgnoreMapping
    var firstName: String? = null,

    @IgnoreMapping
    var middleName: String? = null,

    @IgnoreMapping
    var lastName: String? = null,

    @IgnoreMapping
    var nameSuffix: String? = null,

    @IgnoreMapping
    var phones: List<LabeledValue>? = null,

    @IgnoreMapping
    var emails: List<LabeledValue>? = null,

    @IgnoreMapping
    var groups: List<String>? = null,

    @IgnoreMapping
    var jobTitle: String? = null,

    @IgnoreMapping
    var department: String? = null,

    @IgnoreMapping
    var company: String? = null,

    @IgnoreMapping
    var addresses: List<LabeledValue>? = null,

    @IgnoreMapping
    var importantDates: List<LabeledValue>? = null,

    @IgnoreMapping
    var relationships: List<LabeledValue>? = null,

    @IgnoreMapping
    var notes: String? = null,

    @IgnoreMapping
    var websites: List<String>? = null,

    @IgnoreMapping
    var photoBytes: ByteArray? = null,
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Phone.CONTENT_URI
    }
}
