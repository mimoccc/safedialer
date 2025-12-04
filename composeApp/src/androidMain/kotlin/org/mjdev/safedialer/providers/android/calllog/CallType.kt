package org.mjdev.safedialer.providers.android.calllog

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.CallLog.Calls
import androidx.compose.ui.graphics.Color
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@SuppressLint("InlinedApi")
enum class CallType(
    val value: Int,
    val color: Color = Color.White.copy(alpha = 0.5f)
) : EnumInt {
    UNKNOW(
        0,
        Color.White.copy(alpha = 0.5f)
    ),
    INCOMING(
        Calls.INCOMING_TYPE,
        Color.Green
    ),
    OUTGOING(
        Calls.OUTGOING_TYPE,
        Color.Green
    ),
    VOICEMAIL(
        Calls.VOICEMAIL_TYPE,
        Color.Yellow
    ),
    BLOCKED(
        Calls.BLOCKED_TYPE,
        Color.Red
    ),
    REJECTED(
        Calls.REJECTED_TYPE,
        Color.Red
    ),
    MISSED(
        Calls.MISSED_TYPE,
        Color.Red
    );

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): CallType = entries.firstOrNull { it.value == value } ?: UNKNOW
    }
}