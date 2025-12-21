package org.mjdev.safedialer.providers.custom.document

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import kotlin.jvm.java

@Suppress("unused")
class DocumentsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getDocuments(): List<DocumentItem>? = getContentTableData(safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_documents))
    }, DocumentItem::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_documents))
        }
    ).distinct().filter { it != Uri.EMPTY }
}
