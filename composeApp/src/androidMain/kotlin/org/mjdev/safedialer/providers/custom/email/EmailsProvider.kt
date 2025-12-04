package org.mjdev.safedialer.providers.custom.email

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import kotlin.jvm.java

@Suppress("unused")
class EmailsProvider(
    context: Context
) : AbstractProvider(context) {
//    fun getMailFolders(): List<MailFolder>? = safeUri {
//        Uri.parse("content://" + context.getString(R.string.authority_emails))
//    }.let { uri ->
//        getContentTableData(uri, MailFolder::class.java)
//    }?.getList()

    fun getEmails(): List<MailItem>? = safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_emails))
    }.let { uri ->
        getContentTableData(uri, MailItem::class.java)
    }?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri { Uri.parse("content://" + context.getString(R.string.authority_emails)) }
    ).distinct().filter { it != Uri.EMPTY }
}
