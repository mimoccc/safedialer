package org.mjdev.safedialer.providers.custom.email

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import kotlin.jvm.java

@Suppress("unused")
class EmailsProvider(
    context: Context
) : AbstractProvider(context) {
    fun getMailFolders(): List<MailFolder>? {
        val uri = safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_emails))
        }
        val emails: Data<MailFolder>? = getContentTableData(uri, MailFolder::class.java)
        return emails?.getList()
    }

    fun getEmails(): List<MailItem>? {
        val uri = safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_emails))
        }
        val emails: Data<MailItem>? = getContentTableData(uri, MailItem::class.java)
        return emails?.getList()
    }
}
