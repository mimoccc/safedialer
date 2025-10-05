package org.mjdev.safedialer.providers.android.telephony

import android.net.Uri
import android.provider.Telephony.TextBasedSmsColumns
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class SmsMessageStatus(
    val value: Int
) : EnumInt {
    NONE(TextBasedSmsColumns.STATUS_NONE),
    COMPLETE(TextBasedSmsColumns.STATUS_COMPLETE),
    PENDING(TextBasedSmsColumns.STATUS_PENDING),
    FAILED(TextBasedSmsColumns.STATUS_FAILED);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): SmsMessageStatus? = entries.find { it.value == value }
    }
}