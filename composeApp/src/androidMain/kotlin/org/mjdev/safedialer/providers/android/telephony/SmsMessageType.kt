package org.mjdev.safedialer.providers.android.telephony

import android.net.Uri
import android.provider.Telephony.TextBasedSmsColumns
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class SmsMessageType(
    val value: Int
) : EnumInt {
    ALL(TextBasedSmsColumns.MESSAGE_TYPE_ALL),
    INBOX(TextBasedSmsColumns.MESSAGE_TYPE_INBOX),
    SENT(TextBasedSmsColumns.MESSAGE_TYPE_SENT),
    DRAFT(TextBasedSmsColumns.MESSAGE_TYPE_DRAFT),
    OUTBOX(TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX),
    FAILED(TextBasedSmsColumns.MESSAGE_TYPE_FAILED),
    QUEUED(TextBasedSmsColumns.MESSAGE_TYPE_QUEUED);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): SmsMessageType? = entries.find { it.value == value }
    }
}
