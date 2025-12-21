package org.mjdev.safedialer.providers.android.contacts

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresApi
import org.mjdev.safedialer.providers.core.AbstractProvider
import java.io.ByteArrayInputStream

@RequiresApi(Build.VERSION_CODES.ECLAIR)
class ContactsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getContacts(): List<Contact>? {
        val contactsNoEmail = getContactsNoEmail()
        val contactsWithEmail = getEmailContacts()
        val emailContactsMap = contactsWithEmail?.associateBy { c -> c.contactId } ?: emptyMap()
        val mergedContacts = contactsNoEmail?.map { contact ->
            contact.copy(
                emails = emailContactsMap[contact.contactId]?.emails
            )
        }
        return mergedContacts
    }

    private fun getContactsNoEmail(): List<Contact>? = getContentTableData(
        Contact.uri,
        Contact::class.java
    )?.getList()

    private fun getEmailContacts(): List<Contact>? = getContentTableData(
        Contact.uriEmail,
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
