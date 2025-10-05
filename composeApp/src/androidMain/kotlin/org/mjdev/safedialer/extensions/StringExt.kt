package org.mjdev.safedialer.extensions

object StringExt {
    fun String?.isNotNBlank(): Boolean = this?.isNotBlank() ?: false

    fun String?.removeWhites(
        onErrorValue: String = ""
    ): String = this?.replace(Regex("\\s+"), "") ?: onErrorValue
}