package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import org.mjdev.safedialer.providers.core.AbstractProvider
import androidx.core.net.toUri

@Suppress("unused")
@TargetApi(Build.VERSION_CODES.KITKAT)
class TelephonyProvider(context: Context) : AbstractProvider(context) {
    fun getSms(filter: TelephonyFilter): List<Sms>? = getContentTableData(
        when (filter) {
            TelephonyFilter.ALL -> Sms.uri
            TelephonyFilter.INBOX -> Sms.uriInbox
            TelephonyFilter.OUTBOX -> Sms.uriOutbox
            TelephonyFilter.SENT -> Sms.uriSent
            TelephonyFilter.DRAFT -> Sms.uriDraft
        }, Sms::class.java
    )?.getList()

    fun getMms(filter: TelephonyFilter): List<Mms>? {
        val addressMap = getAllMmsAddresses()
        // todo better mapping if possible
        return getContentTableData(when (filter) {
            TelephonyFilter.ALL -> Mms.uri
            TelephonyFilter.INBOX -> Mms.uriInbox
            TelephonyFilter.OUTBOX -> Mms.uriOutbox
            TelephonyFilter.SENT -> Mms.uriSent
            TelephonyFilter.DRAFT -> Mms.uriDraft
        }, Mms::class.java)?.getList()?.map { mms ->
            mms.copy(
                address = addressMap[mms.id]
            )
        }
    }

    // todo improve
    private fun getAllMmsAddresses(): Map<Long, String> {
        val addressMap = mutableMapOf<Long, String>()
        context.contentResolver.query(
            "content://mms/addr".toUri(),
            arrayOf(Telephony.Mms.Addr.MSG_ID, Telephony.Mms.Addr.ADDRESS, Telephony.Mms.Addr.TYPE),
            "${Telephony.Mms.Addr.TYPE} = ?",
            arrayOf("137"), // FROM type
            null
        )?.use { cursor ->
            val msgIdIndex = cursor.getColumnIndexOrThrow(Telephony.Mms.Addr.MSG_ID)
            val addressIndex = cursor.getColumnIndexOrThrow(Telephony.Mms.Addr.ADDRESS)
            while (cursor.moveToNext()) {
                val msgId = cursor.getLong(msgIdIndex)
                val address = cursor.getString(addressIndex)
                addressMap[msgId] = address
            }
        }
        return addressMap
    }

    fun getConversations(): List<Conversation>? =
        getContentTableData(Conversation.uri, Conversation::class.java)?.getList()

    fun getThreads(): List<Thread>? =
        getContentTableData(Thread.uri, Thread::class.java)?.getList()

    fun getCarriers(): List<Carrier>? =
        getContentTableData(Carrier.uri, Carrier::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        Carrier.uri,
        Conversation.uri,
        Thread.uri,
        Sms.uri,
        Sms.uriSent,
        Sms.uriOutbox,
        Sms.uriDraft,
        Sms.uriInbox,
        Mms.uri,
        Mms.uriSent,
        Mms.uriOutbox,
        Mms.uriDraft,
        Mms.uriInbox
    ).distinct().filter { it != Uri.EMPTY }
}
