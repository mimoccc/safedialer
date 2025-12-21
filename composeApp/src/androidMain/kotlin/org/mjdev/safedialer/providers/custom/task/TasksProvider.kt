package org.mjdev.safedialer.providers.custom.task

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import org.mjdev.safedialer.providers.custom.auth.AuthItem
import org.mjdev.safedialer.providers.custom.invoice.InvoiceItem
import org.mjdev.safedialer.providers.custom.notes.NotesItem
import kotlin.jvm.java

@Suppress("unused")
class TasksProvider(
    context: Context
) : AbstractProvider(context) {
    fun getTasks(): List<TaskItem>? = getContentTableData(safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_tasks))
    }, TaskItem::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_tasks))
        }
    ).distinct().filter { it != Uri.EMPTY }
}
