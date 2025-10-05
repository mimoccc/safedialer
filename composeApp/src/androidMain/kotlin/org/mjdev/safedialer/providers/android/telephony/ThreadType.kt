package org.mjdev.safedialer.providers.android.telephony

import android.net.Uri
import android.provider.Telephony.Threads
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class ThreadType(
    val value: Int
) : EnumInt {
    COMMON(Threads.COMMON_THREAD),
    BROADCAST(Threads.BROADCAST_THREAD);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): ThreadType? = entries.find { it.value == value }
    }
}