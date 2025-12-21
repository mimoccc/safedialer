package org.mjdev.safedialer.extensions

import java.net.URI

object StringExt {
    fun String?.isNotNBlank(): Boolean = this?.isNotBlank() ?: false

    fun String?.removeWhites(
        onErrorValue: String = ""
    ): String = this?.replace(Regex("\\s+"), "") ?: onErrorValue

    fun String.toURI(): URI = URI.create(this)
}