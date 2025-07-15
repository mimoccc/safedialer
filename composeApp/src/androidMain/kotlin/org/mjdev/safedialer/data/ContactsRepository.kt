package org.mjdev.safedialer.data

import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
import android.provider.Telephony
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.mjdev.safedialer.app.MainApp
import org.mjdev.safedialer.data.enums.CallType
import org.mjdev.safedialer.data.model.CallModel
import org.mjdev.safedialer.data.model.ContactModel
import org.mjdev.safedialer.data.model.MessageModel
import org.mjdev.safedialer.extensions.CursorFlow.cursorFlow
import org.mjdev.safedialer.helpers.Cache
import org.mjdev.safedialer.service.IncomingCallService
import org.mjdev.safedialer.service.external.PhoneLookup
import java.util.Date
import kotlin.text.firstOrNull
import android.provider.CallLog.Calls.DATE as CALL_DATE
import android.provider.CallLog.Calls.DURATION as CALL_DURATION
import android.provider.CallLog.Calls.NUMBER as CALL_NUMBER
import android.provider.CallLog.Calls.TYPE as CALL_TYPE
import android.provider.CallLog.Calls._ID as CALL_ID
import android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME as CONTACT_DISPLAY_NAME
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER as CONTACT_NUMBER
import android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI as CONTACT_PHOTO_THUMBNAIL_URI
import android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI as CONTACT_PHOTO_URI

class ContactList : ArrayList<ContactModel>()
class CallLogList : ArrayList<CallModel>()
class MessagesList : ArrayList<MessageModel>()

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class ContactsRepository(
    val context: Context,
    val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job()),
    val cache: Cache = Cache(),
) : DIAware {
    override val di: DI by (context.applicationContext as MainApp).di
    val phoneLookup by instance<PhoneLookup>()

    val contacts = cursorFlow(
        context = context,
        cache = cache,
        uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    ) { uri, cursor ->
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
    }.flowOn(
        Dispatchers.IO
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    val calls = cursorFlow(
        context = context,
        cache = cache,
        uri = CallLog.Calls.CONTENT_URI
    ) { uri, cursor ->
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
                    contact = findContact(phoneNumber),
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
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    val sms = cursorFlow(
        context = context,
        cache = cache,
        uri = SMS_URI
    ) { uri, cursor ->
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
                    contact = findContact(phoneNumber),
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
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    val messagesMap = sms.map { m ->
        m.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    val contactsMap = contacts.map { cl ->
        cl.groupBy { c ->
            c.displayName.firstOrNull()?.uppercase() ?: ""
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    val callLogMap = calls.map { cl ->
        cl.groupBy { c ->
            Date(c.date).let {
                "${it.date}.${it.month + 1}.${it.year + 1900}"
            }
        }
    }.flowOn(
        Dispatchers.IO
    ).shareIn(
        scope = scope,
        started = Eagerly,
        replay = 1
    )

    init {
        CoroutineScope(Dispatchers.IO + Job()).launch {
            runCatching {
                contacts.collectLatest { contacts ->
                    phoneLookup.updateDetails(contacts)
                }
                calls.collectLatest { calls ->
                    phoneLookup.updateDetails(calls)
                }
                sms.collectLatest { sms ->
                    phoneLookup.updateDetails(sms)
                }
            }
        }
    }

    private fun createContact(caller: String?): ContactModel = ContactModel(
        displayName = caller ?: "",
        phoneNumber = caller ?: "",
    )

    fun findContact(
        caller: String?
    ): ContactModel = when {
        caller == null -> createContact(caller)
        else -> runBlocking {
            val callerTrimmed = caller.removeWhites()
            contacts.firstOrNull()?.firstOrNull { contact ->
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

    private fun String.removeWhites() = replace("-", "")
        .replace(" ", "")
        .trim()

    companion object {

        val SMS_URI: Uri = Uri.parse("content://sms/")

        val isDebug: Boolean = IncomingCallService.isStarted.not()

        @Volatile
        private var instance: ContactsRepository? = null

        fun getInstance(
            context: Context
        ): ContactsRepository = synchronized(this) {
            instance ?: ContactsRepository(context).also { instance = it }
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

        val EmptyMap = if (isDebug) mapOf(
            "M" to listOf(
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
                ContactModel(),
            )
        ) else emptyMap()
    }
}
