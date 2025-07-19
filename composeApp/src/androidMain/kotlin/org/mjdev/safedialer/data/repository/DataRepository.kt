package org.mjdev.safedialer.data.repository

import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
import android.provider.Telephony
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.mjdev.safedialer.R
import org.mjdev.safedialer.data.enums.CallType
import org.mjdev.safedialer.data.lists.CallLogList
import org.mjdev.safedialer.data.lists.ContactList
import org.mjdev.safedialer.data.lists.EmailMessageList
import org.mjdev.safedialer.data.lists.MessagesList
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.data.model.EmailMessageModel
import org.mjdev.safedialer.data.model.MessageModel
import org.mjdev.safedialer.extensions.CursorFlow.cursorFlow
import org.mjdev.safedialer.helpers.Cache
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_ID
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_CREATED_AT_MILLIS
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_BODY
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_IS_DELETED
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_IS_FLAGGED
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_MAILBOX_NAME
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_RECIPIENTS_CSV
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SENDER_EMAIL
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SENDER_NAME
import org.mjdev.safedialer.sync.emails.ProviderEmails.Companion.MAIL_ITEM_SUBJECT
import java.util.Date
import kotlin.String
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
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
    val cache: Cache = Cache(),
) : DIAware {
    override val di: DI by closestDI(context)

//    val dao: DAO by instance()
//    val phoneLookup by instance<PhoneLookup>()

    val contacts: Flow<List<ContactModel>> = runCatching {
        cursorFlow(
            context = context,
            cache = cache,
            uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        ) { _, cursor ->
            runCatching {
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
//             todo : group by phone number
                    contact.contactId
                }.sortedBy { contact ->
                    contact.displayName
                }
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull() ?: ContactList()
        }.flowOn(
            Dispatchers.IO
        ).shareIn(scope, Eagerly, 1)
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: flow { emit(ContactList()) }

    val calls: Flow<List<CallModel>> = runCatching {
        cursorFlow(
            context = context,
            cache = cache,
            uri = CallLog.Calls.CONTENT_URI
        ) { _, cursor ->
            runCatching {
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
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull() ?: CallLogList()
        }.flowOn(
            Dispatchers.IO
        ).shareIn(scope, Eagerly, 1)
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: flow { emit(CallLogList()) }

    val sms: Flow<List<MessageModel>> = runCatching {
        cursorFlow(
            context = context,
            cache = cache,
            uri = SMS_URI
        ) { _, cursor ->
            runCatching {
                MessagesList().apply {
                    while (cursor.moveToNext()) {
                        val idIndex = cursor.getColumnIndex(Telephony.Sms._ID)
                        val numberIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
                        val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE_SENT)
                        val phoneNumber = cursor.getString(numberIndex)
                        // todo
                        MessageModel(
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
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull() ?: MessagesList()
        }.flowOn(
            Dispatchers.IO
        ).shareIn(scope, Eagerly, 1)
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: flow { emit(MessagesList()) }

    val emails: Flow<List<EmailMessageModel>> = runCatching {
        val uriPath = "content://" + context.getString(R.string.authority_emails)
        val uri = uriPath.toUri()
        cursorFlow(
            context = context,
            cache = cache,
            uri = uri
        ) { _, cursor ->
            runCatching {
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
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull() ?: EmailMessageList()
        }.flowOn(
            Dispatchers.IO
        ).shareIn(scope, Eagerly, 1)
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: flow {
        emit(EmailMessageList())
    }

    val messagesMap = sms.map { m ->
        m.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    val emailsMap = emails.map { email ->
        email.groupBy { em ->
            em.mailboxName
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    val contactsMap = contacts.map { cl ->
        cl.groupBy { c ->
            c.displayName.firstOrNull()?.uppercase() ?: ""
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    val callLogMap = calls.map { cl ->
        cl.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(scope, Eagerly, 1)

    init {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            runCatching {
                contacts.collectLatest { //contacts ->
                    // phoneLookup.updateDetails(contacts)
                }
                calls.collectLatest { //calls ->
                    // phoneLookup.updateDetails(calls)
                }
                sms.collectLatest { //sms ->
                    // phoneLookup.updateDetails(sms)
                }
                emailsMap.collectLatest {
                    // phoneLookup.updateDetails(sms)
                }
            }
        }
    }

    private fun createContact(caller: String?): ContactModel = ContactModel(
        displayName = caller ?: "",
        phoneNumber = caller ?: "",
    )

    // todo better match ?, and remove blocking calls
    fun findContactByPhone(
        caller: String?
    ): ContactModel = when {
        caller == null -> createContact(caller)
        else -> runBlocking {
            val callerTrimmed = caller.removeWhites()
            contacts.firstOrNull()?.firstOrNull { contact ->
                // todo, parse phone
                val phoneNumber = contact.phoneNumber.trim().removeWhites()
                (phoneNumber.contentEquals(callerTrimmed) ||
                        contact.displayName.contentEquals(callerTrimmed) ||
                        contact.phoneNumber.removeWhites().contentEquals(
                            callerTrimmed,
                            true
                        ))
                // may be different?
            } ?: createContact(caller)
        }
    }

    // todo better match ?, and remove blocking calls
    fun findContactBySender(
        email: String?,
        senderName: String?
    ): ContactModel = when {
        email == null -> createContact(email)
        else -> runBlocking {
            val emailTrimmed = email.removeWhites()
            val senderNameTrimmed = senderName?.trim()
            contacts.firstOrNull()?.firstOrNull { contact ->
                val isEmailMatch = contact.emails.map { e ->
                    e.trim().removeWhites()
                }.contains(emailTrimmed)
                val isSenderNameMatch = senderNameTrimmed?.let { s ->
                    contact.displayName.contains(s, true)
                } ?: false
                isEmailMatch || isSenderNameMatch
                // may be different?
            } ?: createContact(email)
        }
    }

    private fun String.removeWhites() = replace("-", "")
        .replace(" ", "")
        .trim()

    companion object {
        val SMS_URI: Uri = Uri.parse("content://sms/")
//        val isDebug: Boolean = IncomingCallService.isStarted.not()

        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(
            context: Context
        ): DataRepository = synchronized(this) {
            instance ?: DataRepository(context).also { instance = it }
        }

        @Composable
        fun rememberContactsRepository(
            context: Context = LocalContext.current
        ) = remember {
            runCatching {
                getInstance(context)
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull()
        }
    }
}
