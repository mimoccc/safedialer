package org.mjdev.safedialer.data.enums

import android.annotation.SuppressLint
import android.provider.CallLog

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@SuppressLint("InlinedApi")
actual enum class CallType(val id: Int) {
    INCOMING(CallLog.Calls.INCOMING_TYPE),
    OUTGOING(CallLog.Calls.OUTGOING_TYPE),
    MISSED(CallLog.Calls.MISSED_TYPE),
    REJECTED(CallLog.Calls.REJECTED_TYPE),
    BLOCKED(CallLog.Calls.BLOCKED_TYPE),
    VOICEMAIL(CallLog.Calls.VOICEMAIL_TYPE);

    companion object {
        operator fun invoke(
            id: Int
        ): CallType? = entries.firstOrNull { e -> e.id == id }
    }
}