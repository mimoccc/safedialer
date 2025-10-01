package org.mjdev.safedialer.providers.android.contacts

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import java.io.ByteArrayInputStream

@TargetApi(Build.VERSION_CODES.KITKAT)
class ContactsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getContacts(): List<Contact>? {
        val contactsNoEmail = getContactsNoEmail()
        val contactsWithEmail = getEmailContacts()
        val emailContactsMap = contactsWithEmail?.associateBy {
            it.contactId
        } ?: emptyMap()
        val mergedContacts = contactsNoEmail?.map { contact ->
            contact.copy(
                emails =  emailContactsMap[contact.contactId]?.emails
            )
        }
        return mergedContacts
    }

    private fun getContactsNoEmail(): List<Contact>? {
        return getContentTableData(Contact.uri, Contact::class.java)?.getList()
    }

    private fun getEmailContacts() : List<Contact>? {
        return getContentTableData(Contact.uriEmail, Contact::class.java)?.getList()
    }

    fun getPhotoUri(
        context: Context,
        contactId: String
    ): Uri? {
        try {
            val cur = context.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND " + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
                null,
                null
            )
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null // no photo
                }
            } else {
                return null // error in cursor process
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val person =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong())
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
    }

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
        } finally {
            cursor.close()
        }
        return null
    }
}
