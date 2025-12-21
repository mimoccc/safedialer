package org.mjdev.safedialer.providers.custom.invoice

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import org.mjdev.safedialer.providers.custom.auth.AuthItem
import kotlin.jvm.java

@Suppress("unused")
class InvoicesProvider(
    context: Context
) : AbstractProvider(context) {
    fun getInvoices(): List<InvoiceItem>? = getContentTableData(safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_invoices))
    }, InvoiceItem::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_invoices))
        }
    ).distinct().filter { it != Uri.EMPTY }
}
