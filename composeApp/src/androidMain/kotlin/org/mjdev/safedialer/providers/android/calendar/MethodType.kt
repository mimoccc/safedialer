package org.mjdev.safedialer.providers.android.calendar

import android.net.Uri
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("unused")
enum class MethodType(
    val value: Int
) : EnumInt {
    DEFAULT(0),
    ALERT(1),
    EMAIL(2),
    SMS(3),
    ALARM(4);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): MethodType = entries.find { it.value == value } ?: DEFAULT
    }
}