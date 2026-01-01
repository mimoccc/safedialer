package org.mjdev.safedialer.providers.android.contacts

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.GroupMembership
import android.provider.ContactsContract.CommonDataKinds.Note
import android.provider.ContactsContract.CommonDataKinds.Organization
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.CommonDataKinds.Website
import android.util.Log
import androidx.annotation.RequiresApi
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.LabeledValue
import java.io.ByteArrayInputStream

@Suppress("SameParameterValue")
@RequiresApi(Build.VERSION_CODES.ECLAIR)
class ContactsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getContacts(): List<Contact>? = getContactsNoEmail()

    override fun <T : Entity> postProcess(entity: T) {
        if (entity is Contact) {
            fillContactDetails(entity)
        }
    }

    private fun fillContactDetails(contact: Contact) {
        val uri = ContactsContract.Data.CONTENT_URI
        val selection = "${ContactsContract.Data.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contact.contactId.toString())
        val cursor = context.contentResolver.query(uri, null, selection, selectionArgs, null)
        cursor?.use { c ->
            val mimeTypeIndex = c.getColumnIndex(ContactsContract.Data.MIMETYPE)
            if (mimeTypeIndex == -1) return@use
            while (c.moveToNext()) {
                val mimeType = c.getString(mimeTypeIndex)
                when (mimeType) {
                    StructuredName.CONTENT_ITEM_TYPE -> {
                        contact.namePrefix = getStringValue(c, StructuredName.PREFIX)
                        contact.firstName = getStringValue(c, StructuredName.GIVEN_NAME)
                        contact.middleName = getStringValue(c, StructuredName.MIDDLE_NAME)
                        contact.lastName = getStringValue(c, StructuredName.FAMILY_NAME)
                        contact.nameSuffix = getStringValue(c, StructuredName.SUFFIX)
                        contact.phoneticName = getStringValue(c, StructuredName.PHONETIC_NAME)
                    }
                    Phone.CONTENT_ITEM_TYPE -> {
                        val number = getStringValue(c, Phone.NUMBER)
                        if (number != null) {
                            val type = getIntValue(c, Phone.TYPE)
                            val label = getStringValue(c, Phone.LABEL)
                            val phoneList = contact.phones?.toMutableList() ?: mutableListOf()
                            phoneList.add(LabeledValue(number, type, label))
                            contact.phones = phoneList
                        }
                    }
                    Email.CONTENT_ITEM_TYPE -> {
                        val address = getStringValue(c, Email.ADDRESS)
                        if (address != null) {
                            val type = getIntValue(c, Email.TYPE)
                            val label = getStringValue(c, Email.LABEL)
                            val emailList = contact.emails?.toMutableList() ?: mutableListOf()
                            emailList.add(LabeledValue(address, type, label))
                            contact.emails = emailList
                            if (contact.email == null) contact.email = address
                        }
                    }
                    Organization.CONTENT_ITEM_TYPE -> {
                        contact.company = getStringValue(c, Organization.COMPANY)
                        contact.jobTitle = getStringValue(c, Organization.TITLE)
                        contact.department = getStringValue(c, Organization.DEPARTMENT)
                    }
                    StructuredPostal.CONTENT_ITEM_TYPE -> {
                        val address = getStringValue(c, StructuredPostal.FORMATTED_ADDRESS)
                        if (address != null) {
                            val type = getIntValue(c, StructuredPostal.TYPE)
                            val label = getStringValue(c, StructuredPostal.LABEL)
                            val addressList = contact.addresses?.toMutableList() ?: mutableListOf()
                            addressList.add(LabeledValue(address, type, label))
                            contact.addresses = addressList
                        }
                    }
                    Event.CONTENT_ITEM_TYPE -> {
                        val startDate = getStringValue(c, Event.START_DATE)
                        if (startDate != null) {
                            val type = getIntValue(c, Event.TYPE)
                            val label = getStringValue(c, Event.LABEL)
                            val eventList = contact.importantDates?.toMutableList() ?: mutableListOf()
                            eventList.add(LabeledValue(startDate, type, label))
                            contact.importantDates = eventList
                        }
                    }
                    Relation.CONTENT_ITEM_TYPE -> {
                        val name = getStringValue(c, Relation.NAME)
                        if (name != null) {
                            val type = getIntValue(c, Relation.TYPE)
                            val label = getStringValue(c, Relation.LABEL)
                            val relationList = contact.relationships?.toMutableList() ?: mutableListOf()
                            relationList.add(LabeledValue(name, type, label))
                            contact.relationships = relationList
                        }
                    }
                    Note.CONTENT_ITEM_TYPE -> {
                        contact.notes = getStringValue(c, Note.NOTE)
                    }
                    Website.CONTENT_ITEM_TYPE -> {
                        val url = getStringValue(c, Website.URL)
                        if (url != null) {
                            val websiteList = contact.websites?.toMutableList() ?: mutableListOf()
                            websiteList.add(url)
                            contact.websites = websiteList
                        }
                    }
                    GroupMembership.CONTENT_ITEM_TYPE -> {
                        val groupIdIndex = c.getColumnIndex(GroupMembership.GROUP_ROW_ID)
                        if (groupIdIndex != -1) {
                            val groupId = c.getLong(groupIdIndex)
                            val groupName = getGroupName(groupId)
                            if (groupName != null) {
                                val groupList = contact.groups?.toMutableList() ?: mutableListOf()
                                groupList.add(groupName)
                                contact.groups = groupList
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getStringValue(
        cursor:Cursor,
        columnName: String
    ): String? {
        val index = cursor.getColumnIndex(columnName)
        return if (index != -1) cursor.getString(index) else null
    }

    private fun getIntValue(
        cursor: Cursor,
        columnName: String
    ): Int {
        val index = cursor.getColumnIndex(columnName)
        return if (index != -1) cursor.getInt(index) else 0
    }

    private fun getGroupName(
        groupId: Long
    ): String? {
        val uri = ContactsContract.Groups.CONTENT_URI
        val selection = "${ContactsContract.Groups._ID} = ?"
        val selectionArgs = arrayOf(groupId.toString())
        val cursor = context.contentResolver.query(uri, arrayOf(ContactsContract.Groups.TITLE), selection, selectionArgs, null)
        cursor?.use { c ->
            if (c.moveToFirst()) {
                return c.getString(0)
            }
        }
        return null
    }

    private fun getContactsNoEmail(): List<Contact>? = getContentTableData(
        Contact.uri,
        Contact::class.java
    )?.getList()

    @SuppressLint("Recycle")
    fun getPhotoUri(
        context: Context,
        contactId: String
    ): Uri? {
        try {
            val cur = context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " +
                        ContactsContract.Data.MIMETYPE + "='" +
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                null,
                null
            )
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null
                }
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    fun getContactPhoto(
        context: Context,
        contactId: String
    ): Bitmap? {
        val contactUri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        val photoUri =
            Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        val cursor = context.contentResolver.query(
            photoUri,
            arrayOf(ContactsContract.Contacts.Photo.PHOTO), null, null, null
        )
        if (cursor == null) {
            return null
        }
        try {
            if (cursor.moveToFirst()) {
                val data = cursor.getBlob(0)
                if (data != null) {
                    return BitmapFactory.decodeStream(ByteArrayInputStream(data))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        } finally {
            cursor.close()
        }
        return null
    }

    companion object {
        private val TAG = ContactsProvider::class.simpleName
    }

    override fun getUris(): List<Uri> = listOf(
        Contact.uri
    ).distinct().filter { it != Uri.EMPTY }
}
