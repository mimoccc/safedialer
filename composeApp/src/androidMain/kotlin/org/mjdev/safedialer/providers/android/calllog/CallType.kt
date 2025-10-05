package org.mjdev.safedialer.providers.android.calllog

import android.net.Uri
import android.provider.CallLog.Calls
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class CallType(
    val value: Int
) : EnumInt {
    INCOMING(Calls.INCOMING_TYPE),
    OUTGOING(Calls.OUTGOING_TYPE),
    BLOCKED(Calls.BLOCKED_TYPE),
    VOICEMAIL(Calls.VOICEMAIL_TYPE),
    REJECTED(Calls.REJECTED_TYPE),
    MISSED(Calls.MISSED_TYPE);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): CallType? = entries.find { it.value == value }
    }
}