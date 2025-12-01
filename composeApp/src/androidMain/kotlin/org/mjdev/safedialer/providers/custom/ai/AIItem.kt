package org.mjdev.safedialer.providers.custom.ai

import android.net.Uri
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.sync.ai.ProviderAI

data class AIItem(
    @FieldMapping(
        ProviderAI.ITEM_ID,
        FieldMapping.PhysicalType.Long
    )
    val id: Long = 0L,
    @FieldMapping(
        ProviderAI.ITEM_CREATED_AT,
        FieldMapping.PhysicalType.Long
    )
    val createdAtMillis: Long = System.currentTimeMillis()
) : Entity() {
    companion object : CompanionWithUri {
        override val uri: Uri = Uri.EMPTY
    }
}