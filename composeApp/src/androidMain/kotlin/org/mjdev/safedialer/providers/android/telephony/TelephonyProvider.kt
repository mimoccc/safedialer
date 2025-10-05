package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import org.mjdev.safedialer.providers.core.AbstractProvider

@Suppress("unused")
@TargetApi(Build.VERSION_CODES.KITKAT)
class TelephonyProvider(context: Context) : AbstractProvider(context) {
    fun getSms(filter: TelephonyFilter): List<Sms>? {
        val uri = when (filter) {
            TelephonyFilter.ALL -> Sms.uri
            TelephonyFilter.INBOX -> Sms.uriInbox
            TelephonyFilter.OUTBOX -> Sms.uriOutbox
            TelephonyFilter.SENT -> Sms.uriSent
            TelephonyFilter.DRAFT -> Sms.uriDraft
        }
        return getContentTableData(uri, Sms::class.java)?.getList()
    }

    fun getMms(filter: TelephonyFilter): List<Mms>? {
        val uri = when (filter) {
            TelephonyFilter.ALL -> Mms.uri
            TelephonyFilter.INBOX -> Mms.uriInbox
            TelephonyFilter.OUTBOX -> Mms.uriOutbox
            TelephonyFilter.SENT -> Mms.uriSent
            TelephonyFilter.DRAFT -> Mms.uriDraft
        }
        return getContentTableData(uri, Mms::class.java)?.getList()
    }

    fun getConversations(): List<Conversation>? =
        getContentTableData(Conversation.uri, Conversation::class.java)?.getList()

    fun getThreads(): List<Thread>? =
        getContentTableData(Thread.uri, Thread::class.java)?.getList()

    fun getCarriers(): List<Carrier>? =
        getContentTableData(Carrier.uri, Carrier::class.java)?.getList()
}
