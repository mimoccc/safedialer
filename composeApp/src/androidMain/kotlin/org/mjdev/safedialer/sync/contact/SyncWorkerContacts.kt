package org.mjdev.safedialer.sync.contact

import android.content.ContentProviderOperation
import android.content.Context
import android.content.SyncResult
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
import android.util.Log
import org.kodein.di.instance
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import org.mjdev.safedialer.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mjdev.safedialer.helpers.MD5Utils.computeMd5
import org.mjdev.safedialer.helpers.VCFHelper.parseContact
import org.mjdev.safedialer.helpers.VCFHelper.toVcfData
import java.nio.file.Files
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes
import kotlin.use
import org.mjdev.safedialer.BuildConfig

@Suppress("unused")
class SyncWorkerContacts(
    context: Context,
    dirName: String,
    providerAuth: String
) : SyncWorkerWebDav<Contact>(context, dirName, providerAuth) {
    val mainModel: MainViewModel by instance()
    val contacts: List<Contact>
        get() = runBlocking { mainModel.contacts.first() }

    override fun prepareLocalFiles(
        syncResult: SyncResult?
    ) {
        contacts.forEach { contact ->
            val contactId = contact.id.toString()
            val localFileName = "${contactId}.vcf"
            val localFile = privateSyncDir.resolve(localFileName)
            val contactData = toVcfData(contact, context)
            if (localFile.exists()) {
                val localFileData = privateSyncDir.resolve(localFileName).readBytes()
                val md5ContactData = computeMd5(contactData)
                val md5FileData = computeMd5(localFileData)
                if (!md5ContactData.contentEquals(md5FileData)) {
                    localFile.writeBytes(contactData)
                }
            } else {
                localFile.writeBytes(contactData)
            }
        }
    }

    override fun mergeChanges() {
        Files.walk(baseLocalFilesPath).forEach { path ->
            if (path.isDirectory()) {
                // omit
            } else if (path.toFile().absolutePath.contains(".vcf")) {
                val contact = parseContact(path, path.readBytes())
                onAdd(contact)
            } else {
                Log.e(TAG, "Unrecognizable contact file: $path")
            }
        }
    }

    fun onAdd(contact: Contact) {
        if (contactExists(contact)) {
            onUpdate(contact)
        } else {
            storeContactForPackageName(contact)
            submitOnChangeEvent()
        }
    }

    fun onUpdate(contact: Contact) {
        if (contactExists(contact)) {
            updateContactForPackageName(contact)
            submitOnChangeEvent()
        } else {
            onAdd(contact)
        }
    }

    // todo, read contacts first and delete obsoletes
    fun onDelete(contact: Contact) {
        if (contactExists(contact)) {
            deleteContactForPackageName(contact)
            submitOnChangeEvent()
        }
    }

    private fun contactExists(
        contact: Contact,
        accountType : String = BuildConfig.ACCOUNT_TYPE,
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

    private fun findRawContactId(
        contact: Contact,
        accountType: String = BuildConfig.ACCOUNT_TYPE,
        accountName: String = BuildConfig.SERVER_UNAME
    ): Long? {
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
                return cursor.getLong(0)
            }
        }
        return null
    }

    private fun storeContactForPackageName(
        contact: Contact,
        accountType : String = BuildConfig.ACCOUNT_TYPE,
        accountName:String =  BuildConfig.SERVER_UNAME
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
        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateContactForPackageName(
        contact: Contact,
        accountType : String = BuildConfig.ACCOUNT_TYPE,
        accountName:String =  BuildConfig.SERVER_UNAME
    ) {
        val contactId = findRawContactId(
            contact,
            accountType,
            accountName
        ) ?: return
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
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
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
        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteContactForPackageName(
        contact: Contact,
        accountType : String = BuildConfig.ACCOUNT_TYPE,
        accountName:String =  BuildConfig.SERVER_UNAME
    ) {
        val contactId = findRawContactId(
            contact,
            accountType,
            accountName
        )
        if (contactId != null) {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val TAG = SyncWorkerContacts::class.simpleName
    }
}
