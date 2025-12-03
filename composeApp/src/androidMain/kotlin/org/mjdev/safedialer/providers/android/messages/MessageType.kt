package org.mjdev.safedialer.providers.android.messages;

import org.mjdev.safedialer.providers.android.telephony.Mms
import org.mjdev.safedialer.providers.android.telephony.Sms
import kotlin.reflect.KClass

enum class MessageType(
    val type: KClass<*>
) {
    UNKNOWN(Unit::class),
    SMS(Sms::class),
    MMS(Mms::class);

    companion object {
        operator fun invoke(
            message: Any
        ) = entries.firstOrNull { e -> e.type == message::class }
    }
}