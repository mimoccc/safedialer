package org.mjdev.safedialer.sync.contacts

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import org.mjdev.safedialer.sync.SyncManager
import kotlin.collections.plusAssign

object ContactAutoEnricher {
    private val phoneRegex = Regex("(?:\\+?\\d[\\s-()]*){7,}")

    fun enrichFromEmail(
        context: Context,
        displayName: String?,
        emailAddress: String?,
        messageBody: String?
    ) {
        if (emailAddress.isNullOrBlank()) return
        try {
            val resolver = context.contentResolver
            val existingRawId = findRawContactIdByEmail(resolver, emailAddress)
            val ops = ArrayList<ContentProviderOperation>()
            if (existingRawId != null) {
                addPhonesFromBody(existingRawId, messageBody, ops)
            } else {
                val names = splitName(displayName.orEmpty())
                val rawIndex = ops.size
                ops += ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(
                        ContactsContract.RawContacts.ACCOUNT_TYPE,
                        SyncManager.accountType
                    )
                    .withValue(
                        ContactsContract.RawContacts.ACCOUNT_NAME,
                        SyncManager.accountName
                    )
                    .build()
                ops += ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawIndex)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        names.first
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        names.second
                    )
                    .build()
                ops += ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawIndex)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailAddress)
                    .build()
                addPhonesFromBodyBackRef(rawIndex, messageBody, ops)
            }
            if (ops.isNotEmpty()) resolver.applyBatch(ContactsContract.AUTHORITY, ops)
            SyncManager.requestImmediateSync(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findRawContactIdByEmail(
        resolver: ContentResolver,
        email: String
    ): Long? = resolver.query(
        ContactsContract.Data.CONTENT_URI,
        arrayOf(ContactsContract.Data.RAW_CONTACT_ID),
        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.CommonDataKinds.Email.DATA + "=?",
        arrayOf(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE, email),
        null
    )?.use { c ->
        if (c.moveToFirst()) c.getLong(0) else null
    }

    private fun addPhonesFromBody(
        rawId: Long,
        body: String?,
        ops: MutableList<ContentProviderOperation>
    ) {
        if (body.isNullOrBlank()) return
        val phones = phoneRegex.findAll(body).map { it.value.trim() }.take(3).toSet()
        phones.forEach { number ->
            ops += ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawId)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build()
        }
    }

    private fun addPhonesFromBodyBackRef(
        rawIndex: Int,
        body: String?,
        ops: MutableList<ContentProviderOperation>
    ) {
        if (body.isNullOrBlank()) return
        val phones = phoneRegex.findAll(body).map { it.value.trim() }.take(3).toSet()
        phones.forEach { number ->
            ops += ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawIndex)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build()
        }
    }

    private fun splitName(
        displayName: String
    ): Pair<String, String> {
        val parts = displayName.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "" to ""
            parts.size == 1 -> parts[0] to ""
            else -> parts.first() to parts.drop(1).joinToString(" ")
        }
    }

}
