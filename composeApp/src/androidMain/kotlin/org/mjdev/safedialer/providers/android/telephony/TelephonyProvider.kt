package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri
import android.os.Build
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

@TargetApi(Build.VERSION_CODES.KITKAT)
class TelephonyProvider(context: Context) : AbstractProvider(context) {
    enum class Filter {
        ALL,
        INBOX,
        OUTBOX,
        SENT,
        DRAFT
    }

    fun getSms(filter: Filter): Data<Sms>? {
        val uri = when (filter) {
            Filter.ALL -> Sms.uri
            Filter.INBOX -> Sms.uriInbox
            Filter.OUTBOX -> Sms.uriOutbox
            Filter.SENT -> Sms.uriSent
            Filter.DRAFT -> Sms.uriDraft
        }
        return getContentTableData(uri, Sms::class.java)
    }

    fun getMms(filter: Filter): Data<Mms>? {
        val uri = when (filter) {
            Filter.ALL -> Mms.uri
            Filter.INBOX -> Mms.uriInbox
            Filter.OUTBOX -> Mms.uriOutbox
            Filter.SENT -> Mms.uriSent
            Filter.DRAFT -> Mms.uriDraft
        }
        return getContentTableData(uri, Mms::class.java)
    }

    fun getConversations(): Data<Conversation>? =
        getContentTableData(Conversation.uri, Conversation::class.java)

    fun getThreads(): Data<Thread>? = getContentTableData(Thread.uri, Thread::class.java)
    fun getCarriers(): Data<Carrier>? = getContentTableData(Carrier.uri, Carrier::class.java)
}
