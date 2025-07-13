package org.mjdev.safedialer.dao

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.mjdev.safedialer.dao.base.IDAO
import org.mjdev.safedialer.data.model.MetaData
import org.mjdev.safedialer.R
import org.mjdev.safedialer.dao.base.DAOCollection

class DAO(
    val context: Context
) : IDAO(
    context.getString(R.string.app_name)
) {
    val meta: DAOCollection<MetaData> by this

    companion object {
        @Volatile
        private var instance: DAO? = null

        fun getInstance(
            context: Context
        ): DAO = synchronized(this) {
            instance ?: DAO(context).also { dao -> instance = dao }
        }

        @Composable
        fun rememberDAO(
            context: Context = LocalContext.current
        ) = remember {
            runCatching {
                getInstance(context)
            }.onFailure { e ->
                e.printStackTrace()
            }.getOrNull()
        }
    }
}
