package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.Telephony.Sms.Conversations
import android.provider.Telephony.TextBasedSmsColumns
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@TargetApi(Build.VERSION_CODES.KITKAT)
data class Conversation(
    @FieldMapping(
        columnName = TextBasedSmsColumns.THREAD_ID,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var threadId: Int = 0,

    @FieldMapping(
        columnName = Conversations.MESSAGE_COUNT,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var messageCount: Int = 0,

    @FieldMapping(
        columnName = Conversations.SNIPPET,
        physicalType = FieldMapping.PhysicalType.String
    )
    var snippet: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Conversations.CONTENT_URI
    }
}
