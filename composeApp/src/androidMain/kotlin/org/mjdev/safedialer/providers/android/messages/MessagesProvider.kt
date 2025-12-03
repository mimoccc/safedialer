package org.mjdev.safedialer.providers.android.messages

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import org.mjdev.safedialer.providers.core.safeUri
import kotlin.jvm.java

@Suppress("unused")
class MessagesProvider(
    context: Context
) : AbstractProvider(context) {
    fun getThreads(): List<MessageThread>? {
        val uri = safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_messages))
        }
        val threads: Data<MessageThread>? = getContentTableData(uri, MessageThread::class.java)
        return threads?.getList()
    }

    override fun getUris(): List<Uri> = listOf(
        MessageThread.uri
    ).distinct().filter { it != Uri.EMPTY }
}
