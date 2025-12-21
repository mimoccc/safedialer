package org.mjdev.safedialer.providers.custom.task

import android.net.Uri
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.sync.task.ProviderTasks

data class TaskItem(
    @FieldMapping(
        ProviderTasks.ITEM_ID,
        FieldMapping.PhysicalType.Long
    )
    val id: Long = 0L,
    @FieldMapping(
        ProviderTasks.ITEM_CREATED_AT,
        FieldMapping.PhysicalType.Long
    )
    val createdAtMillis: Long = System.currentTimeMillis()
) : Entity() {
    companion object : CompanionWithUri {
        override val uri: Uri = Uri.EMPTY
    }
}
