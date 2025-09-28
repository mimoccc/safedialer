package org.mjdev.safedialer.dao

import android.content.Context
import kotbase.DatabaseConfigurationFactory
import kotbase.newConfig
import org.mjdev.safedialer.R
import org.mjdev.safedialer.dao.base.DAOCollection
import org.mjdev.safedialer.dao.base.IDAO
import org.mjdev.safedialer.providers.custom.email.MailItem

class DAO(
    val context: Context
) : IDAO(
    dbName = context.getString(R.string.app_name),
    config = DatabaseConfigurationFactory.newConfig(context.filesDir.absolutePath)
) {
    val emails: DAOCollection<MailItem> by this
}
