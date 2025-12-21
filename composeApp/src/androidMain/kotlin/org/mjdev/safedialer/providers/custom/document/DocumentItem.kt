package org.mjdev.safedialer.providers.custom.document

import android.net.Uri
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.sync.authenticator.ProviderAuthenticator
import org.mjdev.safedialer.sync.document.ProviderDocuments

data class DocumentItem(
    @FieldMapping(
        ProviderDocuments.ITEM_ID,
        FieldMapping.PhysicalType.Long
    )
    val id: Long = 0L,
    @FieldMapping(
        ProviderDocuments.ITEM_CREATED_AT,
        FieldMapping.PhysicalType.Long
    )
    val createdAtMillis: Long = System.currentTimeMillis()
) : Entity() {
    companion object : CompanionWithUri {
        override val uri: Uri = Uri.EMPTY
    }
}
