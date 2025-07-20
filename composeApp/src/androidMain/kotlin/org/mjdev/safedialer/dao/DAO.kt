package org.mjdev.safedialer.dao

import android.content.Context
import org.mjdev.safedialer.R
import org.mjdev.safedialer.dao.base.DAOCollection
import org.mjdev.safedialer.dao.base.IDAO
import org.mjdev.safedialer.data.model.MetaData

class DAO(
    val context: Context
) : IDAO(
    context.getString(R.string.app_name)
) {
    val meta: DAOCollection<MetaData> by this
}
