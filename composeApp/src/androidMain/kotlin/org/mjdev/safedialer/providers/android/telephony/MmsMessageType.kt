package org.mjdev.safedialer.providers.android.telephony

import android.net.Uri
import android.provider.Telephony.BaseMmsColumns
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class MmsMessageType(
    val value: Int
) : EnumInt {
    ALL(BaseMmsColumns.MESSAGE_BOX_ALL),
    INBOX(BaseMmsColumns.MESSAGE_BOX_INBOX),
    SENT(BaseMmsColumns.MESSAGE_BOX_SENT),
    DRAFT(BaseMmsColumns.MESSAGE_BOX_DRAFTS),
    OUTBOX(BaseMmsColumns.MESSAGE_BOX_OUTBOX);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): MmsMessageType? = entries.find { it.value == value }
    }
}
