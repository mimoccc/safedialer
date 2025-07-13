package org.mjdev.safedialer.data.enums

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect enum class CallType {
    INCOMING,
    OUTGOING,
    MISSED,
    REJECTED,
    BLOCKED,
    VOICEMAIL
}