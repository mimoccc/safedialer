package org.mjdev.safedialer.extensions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateExt {
    @SuppressLint("SimpleDateFormat")
    fun Long?.formatDate(
        format: String = "dd.MM.yyyy",
        errorValue: String = "-"
    ): String = runCatching {
        if (this == null) null else {
            val sdf = SimpleDateFormat(format)
            sdf.format(Date(this))
        }
    }.getOrNull() ?: errorValue
}