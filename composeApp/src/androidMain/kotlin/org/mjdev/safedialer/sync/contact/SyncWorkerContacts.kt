package org.mjdev.safedialer.sync.contact

import android.content.Context
import android.content.SyncResult
import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.kodein.di.instance
import org.mjdev.safedialer.helpers.ContactHelper.contactExists
import org.mjdev.safedialer.helpers.ContactHelper.deleteContactForPackageName
import org.mjdev.safedialer.helpers.ContactHelper.storeContactForPackageName
import org.mjdev.safedialer.helpers.ContactHelper.updateContactForPackageName
import org.mjdev.safedialer.helpers.MD5Utils.computeMd5Bytes
import org.mjdev.safedialer.helpers.Merger
import org.mjdev.safedialer.helpers.VCFHelper.parseContact
import org.mjdev.safedialer.helpers.VCFHelper.toVcfData
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.sync.SyncWorkerWebDav
import org.mjdev.safedialer.viewmodel.MainViewModel
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

@Suppress("unused")
class SyncWorkerContacts(
    context: Context,
    dirName: String,
) : SyncWorkerWebDav<Contact>(context, dirName) {
    val mainModel: MainViewModel by instance()
    val syncDir = privateSyncDir
    val phoneContacts: List<Contact>
        get() = runBlocking {
            try {
                mainModel.contacts.first()
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    Log.e(TAG, "Error getting contacts: ${e.message}")
                }
                emptyList()
            }
        }
    val webDavContacts: List<Contact>
        get() = runBlocking {
            syncDir.listDirectoryEntries().mapNotNull { vcard ->
                runCatching {
                    parseContact(vcard, vcard.readBytes())
                }.getOrNull()
            }
        }

    override fun prepareLocalFiles(syncResult: SyncResult?) {
        Log.d(TAG, "prepareLocalFiles: starting, phoneContacts.size=${phoneContacts.size}")
        phoneContacts.forEach { contact ->
            val contactId = contact.id.toString()
            val localFileName = "${contactId}.vcf"
            val localFile = syncDir.resolve(localFileName)
            Log.d(TAG, "Processing contact: ${contact.displayName} (ID: $contactId)")
            val contactData = toVcfData(contact, context)
            if (localFile.exists()) {
                val localFileData = localFile.readBytes()
                val md5ContactData = computeMd5Bytes(contactData)
                val md5FileData = computeMd5Bytes(localFileData)
                if (!md5ContactData.contentEquals(md5FileData)) {
                    Log.d(TAG, "Contact changed, updating file: $localFileName")
                    localFile.writeBytes(contactData)
                }
            } else {
                Log.d(TAG, "New contact, creating file: $localFileName")
                localFile.writeBytes(contactData)
            }
        }
        Log.d(TAG, "prepareLocalFiles: completed")
    }

    override fun mergeChanges() {
        Log.d(TAG, "Merging changes from $syncDir")
        syncDir.listDirectoryEntries("*.vcf.d").forEach { deletionFile ->
            val contactId = deletionFile.fileName.toString()
                .removeSuffix(".vcf.d")
                .toLongOrNull()
            if (contactId != null) {
                val phoneContact = phoneContacts.find { it.id == contactId }
                if (phoneContact != null) {
                    Log.d(TAG, "Deletion marker found on WebDAV, deleting from phone: $contactId")
                    removeContact(phoneContact)
                }
                deletionFile.deleteIfExists()
            }
        }
        phoneContacts.forEach { phoneContact ->
            val matchingWebDav = webDavContacts.find { it.id == phoneContact.id }
            if (matchingWebDav == null) {
                Log.d(TAG, "NEW phone contact -> uploading to WebDAV: ${phoneContact.displayName}")
                val localFileName = "${phoneContact.id}.vcf"
                val localFile = syncDir.resolve(localFileName)
                val contactData = toVcfData(phoneContact, context)
                localFile.writeBytes(contactData)
            }
        }
        Merger(
            firstList = phoneContacts,
            secondList = webDavContacts,
            comparer = { phoneContact, webDavContact ->
                if (phoneContact.id == webDavContact.id) {
                    val phoneData = toVcfData(phoneContact, context)
                    val localFileName = "${webDavContact.id}.vcf"
                    val localFile = syncDir.resolve(localFileName)
                    val webDavData = if (localFile.exists()) {
                        localFile.readBytes()
                    } else {
                        toVcfData(webDavContact, context)
                    }
                    val phoneHash = computeMd5Bytes(phoneData)
                    val webDavHash = computeMd5Bytes(webDavData)
                    if (phoneHash.contentEquals(webDavHash)) {
                        Merger.MergerAction.NOTHING
                    } else {
                        Merger.MergerAction.UPDATE
                    }
                } else {
                    Merger.MergerAction.INSERT
                }
            },
            inserts = { contacts ->
                Log.d(TAG, "NEW WebDAV contacts -> adding to phone: ${contacts.size}")
                contacts.forEach { contact -> addContact(contact) }
            },
            updates = { contacts ->
                Log.d(TAG, "CHANGED contacts -> updating phone: ${contacts.size}")
                contacts.forEach { contact -> updateContact(contact) }
            },
            removals = { contacts ->
                Log.d(TAG, "Phone-only contacts: ${contacts.size} (already uploaded)")
            }
        ).merge()
    }

    fun addContact(contact: Contact) {
        Log.d(TAG, "Adding contact: ${contact.displayName} (ID: ${contact.id})")
        if (contactExists(context, contact)) {
            updateContact(contact)
        } else {
            storeContactForPackageName(context, contact) { stored ->
//                if (stored) {
//                    submitOnChangeEvent(contact.id)
//                }
            }
        }
    }

    fun updateContact(contact: Contact) {
        Log.d(TAG, "Updating contact: ${contact.displayName} (ID: ${contact.id})")
        if (contactExists(context, contact)) {
            updateContactForPackageName(context, contact) { updated ->
//                if (updated) {
//                    submitOnChangeEvent(contact.id)
//                }
            }
        } else {
            addContact(contact)
        }
    }

    fun removeContact(contact: Contact) {
        Log.d(TAG, "Removing contact: ${contact.displayName} (ID: ${contact.id})")
        if (contactExists(context, contact)) {
            deleteContactForPackageName(context, contact) { deleted ->
                if (deleted) {
                    val deletionMarker = syncDir.resolve("${contact.id}.vcf.d")
                    deletionMarker.writeBytes(System.currentTimeMillis().toString().toByteArray())
                    val vcfFile = syncDir.resolve("${contact.id}.vcf")
                    vcfFile.deleteIfExists()
//                    submitOnChangeEvent(contact.id)
                }
            }
        }
    }

    companion object {
        private val TAG = SyncWorkerContacts::class.simpleName
    }
}
