package org.mjdev.safedialer.providers.custom.ai

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import org.mjdev.safedialer.providers.custom.email.MailFolder
import org.mjdev.safedialer.providers.custom.email.MailItem
import kotlin.jvm.java

@Suppress("unused")
class AIProvider(
    context: Context
) : AbstractProvider(context) {
    fun getAiPrompts(): List<AIItem>? {
        val uri = safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_ai))
        }
        val emails: Data<AIItem>? = getContentTableData(uri, AIItem::class.java)
        return emails?.getList()
    }

    override fun getUris(): List<Uri> = listOf(
        safeUri { Uri.parse("content://" + context.getString(R.string.authority_ai)) }
    ).distinct().filter { it != Uri.EMPTY }
}
