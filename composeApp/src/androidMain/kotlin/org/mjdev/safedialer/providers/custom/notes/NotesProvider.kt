package org.mjdev.safedialer.providers.custom.notes

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import org.mjdev.safedialer.providers.custom.auth.AuthItem
import org.mjdev.safedialer.providers.custom.invoice.InvoiceItem
import kotlin.jvm.java

@Suppress("unused")
class NotesProvider(
    context: Context
) : AbstractProvider(context) {
    fun getNotesItems(): List<NotesItem>? = getContentTableData(safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_notes))
    }, NotesItem::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_notes))
        }
    ).distinct().filter { it != Uri.EMPTY }
}
