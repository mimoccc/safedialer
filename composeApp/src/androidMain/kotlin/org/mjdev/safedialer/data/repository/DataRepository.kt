package org.mjdev.safedialer.data.repository

import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
import android.provider.Telephony
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import org.mjdev.safedialer.R
import org.mjdev.safedialer.data.enums.CallType
import org.mjdev.safedialer.data.lists.CallLogList
import org.mjdev.safedialer.data.lists.ContactList
import org.mjdev.safedialer.data.lists.EmailMessageList
import org.mjdev.safedialer.data.lists.TextMessagesList
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.data.model.EmailMessageModel
import org.mjdev.safedialer.data.model.TextMessageModel
import org.mjdev.safedialer.data.repository.base.DataRepositoryUtils.findContactByPhone
import org.mjdev.safedialer.data.repository.base.DataRepositoryUtils.findContactBySender
import org.mjdev.safedialer.data.repository.base.IDataRepository
import org.mjdev.safedialer.extensions.CursorFlow.cursorFlow
import org.mjdev.safedialer.extensions.CustomExt.SMS_URI
import org.mjdev.safedialer.helpers.Cache
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_BODY
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_CREATED_AT_MILLIS
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_ID
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_IS_DELETED
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_IS_FLAGGED
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_MAILBOX_NAME
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_RECIPIENTS_CSV
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SENDER_EMAIL
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SENDER_NAME
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SUBJECT
import android.provider.CallLog.Calls.DATE as CALL_DATE
import android.provider.CallLog.Calls.DURATION as CALL_DURATION
import android.provider.CallLog.Calls.NUMBER as CALL_NUMBER
import android.provider.CallLog.Calls.TYPE as CALL_TYPE
import android.provider.CallLog.Calls._ID as CALL_ID
import android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME as CONTACT_DISPLAY_NAME
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER as CONTACT_NUMBER
import android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI as CONTACT_PHOTO_THUMBNAIL_URI
import android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI as CONTACT_PHOTO_URI

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class DataRepository(
    context: Context,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
    cache: Cache = Cache(),
) : IDataRepository(context, scope, cache) {

    override fun getContacts(): Flow<List<ContactModel>> = cursorFlow(
        context = context,
        cache = cache,
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    ) { _, cursor ->
        ContactList().apply {
            val idIndex = cursor.getColumnIndex(CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(CONTACT_DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(CONTACT_NUMBER)
            val photoUriIndex = cursor.getColumnIndex(CONTACT_PHOTO_URI)
            val photoThumbNailIndex = cursor.getColumnIndex(CONTACT_PHOTO_THUMBNAIL_URI)
            while (cursor.moveToNext()) {
                val phoneNum = cursor.getString(numberIndex)
                ContactModel(
                    contactId = cursor.getString(idIndex),
                    displayName = cursor.getString(nameIndex),
                    phoneNumber = phoneNum,
                    photoThumbnailUri = cursor.getString(photoThumbNailIndex),
                    photoUri = cursor.getString(photoUriIndex),
                    isBlocked = false, // todo
                ).also { contact ->
                    add(contact)
                }
            }
        }.filter { pn ->
            pn.displayName.isNotBlank() && pn.phoneNumber.isNotBlank()
        }.distinctBy { contact ->
            contact.contactId
        }.sortedBy { contact ->
            contact.displayName
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    override fun getCalls(): Flow<List<CallModel>> = cursorFlow(
        context = context,
        cache = cache,
        uri = CallLog.Calls.CONTENT_URI
    ) { _, cursor ->
        CallLogList().apply {
            val idIndex = cursor.getColumnIndex(CALL_ID)
            val numberIndex = cursor.getColumnIndex(CALL_NUMBER)
            val typeIndex = cursor.getColumnIndex(CALL_TYPE)
            val dateIndex = cursor.getColumnIndex(CALL_DATE)
            val durationIndex = cursor.getColumnIndex(CALL_DURATION)
            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberIndex)
                CallModel(
                    callId = cursor.getString(idIndex),
                    phoneNumber = phoneNumber,
                    type = CallType(Integer.parseInt(cursor.getString(typeIndex))),
                    date = cursor.getString(dateIndex).toLong(),
                    duration = cursor.getString(durationIndex).toLong(),
                    contact = findContactByPhone(phoneNumber),
                    // details = contact.details, // todo
                ).also { call ->
                    add(call)
                }
            }
        }.sortedByDescending { pn ->
            pn.date
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    override fun getTextMessages(): Flow<List<TextMessageModel>> = cursorFlow(
        context = context,
        cache = cache,
        uri = SMS_URI
    ) { _, cursor ->
        TextMessagesList().apply {
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(Telephony.Sms._ID)
                val numberIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
                val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE_SENT)
                val phoneNumber = cursor.getString(numberIndex)
                TextMessageModel(
                    smsId = cursor.getString(idIndex),
                    phoneNumber = phoneNumber,
                    contact = findContactByPhone(phoneNumber),
                    date = cursor.getString(dateIndex).toLong(),
                ).also { m ->
                    add(m)
                }
            }
        }.sortedByDescending { m ->
            m.date
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    override fun getEmails(): Flow<List<EmailMessageModel>> = cursorFlow(
        context = context,
        cache = cache,
        uri = Uri.parse("content://" + context.getString(R.string.authority_emails))
    ) { _, cursor ->
        EmailMessageList().apply {
            val idxIndex = cursor.getColumnIndex(MAIL_ITEM_ID)
            val idxDate = cursor.getColumnIndex(MAIL_ITEM_CREATED_AT_MILLIS)
            val idxBody = cursor.getColumnIndex(MAIL_ITEM_BODY)
            val idxDeleted = cursor.getColumnIndex(MAIL_ITEM_IS_DELETED)
            val idxFlagged = cursor.getColumnIndex(MAIL_ITEM_IS_FLAGGED)
            val idxBoxName = cursor.getColumnIndex(MAIL_ITEM_MAILBOX_NAME)
            val idxRecipients = cursor.getColumnIndex(MAIL_ITEM_RECIPIENTS_CSV)
            val idxSenderEmail = cursor.getColumnIndex(MAIL_ITEM_SENDER_EMAIL)
            val idxSenderName = cursor.getColumnIndex(MAIL_ITEM_SENDER_NAME)
            val idxSubject = cursor.getColumnIndex(MAIL_ITEM_SUBJECT)
            while (cursor.moveToNext()) {
                val senderEmail = cursor.getString(idxSenderEmail)
                val senderName = cursor.getString(idxSenderName)
                EmailMessageModel(
                    id = cursor.getLong(idxIndex),
                    date = cursor.getLong(idxDate),
                    subject = cursor.getString(idxSubject),
                    body = cursor.getString(idxBody),
                    phoneNumber = senderEmail,
                    senderEmail = senderEmail,
                    senderName = senderName,
                    displayName = senderName,
                    recipients = cursor.getString(idxRecipients).split(","),
                    mailboxName = cursor.getString(idxBoxName),
                    isDeleted = cursor.getString(idxDeleted).toBoolean(),
                    isFlagged = cursor.getString(idxFlagged).toBoolean(),
                    contact = findContactBySender(senderEmail, senderName),
                ).also { email ->
                    add(email)
                }
            }
        }.sortedByDescending { pn ->
            pn.date
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

}
