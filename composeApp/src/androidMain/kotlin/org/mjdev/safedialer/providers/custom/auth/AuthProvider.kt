package org.mjdev.safedialer.providers.custom.auth

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.R
import org.mjdev.safedialer.providers.core.safeUri
import kotlin.jvm.java

@Suppress("unused")
class AuthProvider(
    context: Context
) : AbstractProvider(context) {
    fun getAuthItems(): List<AuthItem>? = getContentTableData(safeUri {
        Uri.parse("content://" + context.getString(R.string.authority_authenticator))
    }, AuthItem::class.java)?.getList()

    override fun getUris(): List<Uri> = listOf(
        safeUri {
            Uri.parse("content://" + context.getString(R.string.authority_authenticator))
        }
    ).distinct().filter { it != Uri.EMPTY }
}
