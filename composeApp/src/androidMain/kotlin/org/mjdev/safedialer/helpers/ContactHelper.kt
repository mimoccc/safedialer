package org.mjdev.safedialer.helpers

import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Note
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.CommonDataKinds.Website
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.providers.android.contacts.Contact
import kotlin.use

object ContactHelper {

    fun contactExists(
        context:Context,
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME
    ): Boolean {
        val uri = ContactsContract.RawContacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = "${ContactsContract.RawContacts.SOURCE_ID} = ? AND " +
                "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ? AND " +
                "${ContactsContract.RawContacts.ACCOUNT_NAME} = ?"
        val selectionArgs = arrayOf(
            contact.id.toString(),
            accountType,
            accountName
        )
        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            return cursor.count > 0
        }
        return false
    }

    fun findRawContactId(
        context:Context,
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME
    ): Long {
        val uri = ContactsContract.RawContacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.RawContacts._ID)
        val selection = "${ContactsContract.RawContacts.SOURCE_ID} = ? AND " +
                "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ? AND " +
                "${ContactsContract.RawContacts.ACCOUNT_NAME} = ?"
        val selectionArgs = arrayOf(
            contact.id.toString(),
            accountType,
            accountName
        )
        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val contactId = cursor.getLong(0)
                return contactId
            }
        }
        return -1
    }

    fun storeContactForPackageName(
        context: Context,
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME,
        onInserted: (Boolean) -> Unit
    ) {
        val ops = arrayListOf<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, accountName)
                .withValue(ContactsContract.RawContacts.SOURCE_ID, contact.id.toString())
                .build()
        )
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, contact.displayName)
                .withValue(StructuredName.GIVEN_NAME, contact.firstName)
                .withValue(StructuredName.FAMILY_NAME, contact.lastName)
                .withValue(StructuredName.MIDDLE_NAME, contact.middleName)
                .withValue(StructuredName.PREFIX, contact.namePrefix)
                .withValue(StructuredName.SUFFIX, contact.nameSuffix)
                .withValue(StructuredName.PHONETIC_GIVEN_NAME, contact.phoneticName)
                .build()
        )
        contact.phones?.forEach { phone ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, phone.value)
                    .withValue(Phone.TYPE, phone.type)
                    .build()
            )
        }
        contact.emails?.forEach { email ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.ADDRESS, email.value)
                    .withValue(Email.TYPE, email.type)
                    .build()
            )
        }
        if (contact.company != null || contact.jobTitle != null) {
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
                    .withValue(Organization.COMPANY, contact.company)
                    .withValue(Organization.TITLE, contact.jobTitle)
                    .withValue(Organization.TYPE, Organization.TYPE_WORK)
                    .build()
            )
        }
        contact.notes?.let { note ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                    .withValue(Note.NOTE, note)
                    .build()
            )
        }
        contact.addresses?.forEach { address ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(StructuredPostal.STREET, address.value)
                    .withValue(StructuredPostal.TYPE, address.type)
                    .build()
            )
        }
        contact.websites?.forEach { website ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
                    .withValue(Website.URL, website)
                    .withValue(Website.TYPE, Website.TYPE_OTHER)
                    .build()
            )
        }
        contact.importantDates?.forEach { date ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
                    .withValue(Event.START_DATE, date.value)
                    .withValue(Event.TYPE, date.type)
                    .build()
            )
        }
        contact.relationships?.forEach { rel ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Relation.CONTENT_ITEM_TYPE)
                    .withValue(Relation.NAME, rel.value)
                    .withValue(Relation.TYPE, rel.type)
                    .build()
            )
        }
        contact.photoBytes?.let { photo ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                    .build()
            )
        }
        contact.groups?.forEach { groupName ->
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                        groupName
                    )
                    .build()
            )
        }
        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            onInserted(true)
        } catch (e: Exception) {
            e.printStackTrace()
            onInserted(false)
        }
    }

    fun updateContactForPackageName(
        context:Context,
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME,
        onUpdateFinished: (Boolean) -> Unit
    ) {
        val contactId = findRawContactId(
            context,
            contact,
            accountType,
            accountName
        )
        if (contactId == -1L) {
            onUpdateFinished(false)
        } else {
            val ops = arrayListOf<ContentProviderOperation>()
            ops.add(
                ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI).withSelection(
                    "${ContactsContract.Data.RAW_CONTACT_ID} = ?",
                    arrayOf(contactId.toString())
                ).build()
            )
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, contact.displayName)
                    .withValue(StructuredName.GIVEN_NAME, contact.firstName)
                    .withValue(StructuredName.FAMILY_NAME, contact.lastName)
                    .withValue(StructuredName.MIDDLE_NAME, contact.middleName)
                    .withValue(StructuredName.PREFIX, contact.namePrefix)
                    .withValue(StructuredName.SUFFIX, contact.nameSuffix)
                    .withValue(StructuredName.PHONETIC_GIVEN_NAME, contact.phoneticName)
                    .build()
            )
            contact.phones?.forEach { phone ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, phone.value)
                        .withValue(Phone.TYPE, phone.type)
                        .build()
                )
            }
            contact.emails?.forEach { email ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                        .withValue(Email.ADDRESS, email.value)
                        .withValue(Email.TYPE, email.type)
                        .build()
                )
            }
            if (contact.company != null || contact.jobTitle != null) {
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Organization.CONTENT_ITEM_TYPE)
                        .withValue(Organization.COMPANY, contact.company)
                        .withValue(Organization.TITLE, contact.jobTitle)
                        .withValue(Organization.TYPE, Organization.TYPE_WORK)
                        .build()
                )
            }
            contact.notes?.let { note ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Note.CONTENT_ITEM_TYPE)
                        .withValue(Note.NOTE, note)
                        .build()
                )
            }
            contact.addresses?.forEach { address ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            StructuredPostal.CONTENT_ITEM_TYPE
                        )
                        .withValue(StructuredPostal.STREET, address.value)
                        .withValue(StructuredPostal.TYPE, address.type)
                        .build()
                )
            }
            contact.websites?.forEach { website ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
                        .withValue(Website.URL, website)
                        .withValue(Website.TYPE, Website.TYPE_OTHER)
                        .build()
                )
            }
            contact.importantDates?.forEach { date ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
                        .withValue(Event.START_DATE, date.value)
                        .withValue(Event.TYPE, date.type)
                        .build()
                )
            }
            contact.relationships?.forEach { rel ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(ContactsContract.Data.MIMETYPE, Relation.CONTENT_ITEM_TYPE)
                        .withValue(Relation.NAME, rel.value)
                        .withValue(Relation.TYPE, rel.type)
                        .build()
                )
            }
            contact.photoBytes?.let { photo ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, photo)
                        .build()
                )
            }
            contact.groups?.forEach { groupName ->
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
                        )
                        .withValue(
                            ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
                            groupName
                        )
                        .build()
                )
            }
            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                onUpdateFinished(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onUpdateFinished(false)
            }
        }
    }

    fun deleteContactForPackageName(
        context: Context,
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME,
        onDeleted: (Boolean) -> Unit
    ) {
        val contactId = findRawContactId(
            context,
            contact,
            accountType,
            accountName
        )
        if (contactId == -1L) {
            onDeleted(false)
        } else {
            val ops = arrayListOf<ContentProviderOperation>()
            ops.add(
                ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                        "${ContactsContract.RawContacts._ID} = ? AND " +
                                "${ContactsContract.RawContacts.ACCOUNT_TYPE} = ? AND " +
                                "${ContactsContract.RawContacts.ACCOUNT_NAME} = ?",
                        arrayOf(
                            contactId.toString(),
                            accountType,
                            accountName
                        )
                    )
                    .build()
            )
            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                onDeleted(true)
            } catch (e: Exception) {
                onDeleted(false)
                e.printStackTrace()
            }
        }
    }
}