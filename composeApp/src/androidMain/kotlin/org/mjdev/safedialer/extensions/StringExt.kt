package org.mjdev.safedialer.extensions

import org.jsoup.Jsoup
import java.net.URI

@Suppress("unused")
object StringExt {

    fun String?.isNotNBlank(): Boolean =
        this?.isNotBlank() ?: false

    fun String?.removeWhites(
        onErrorValue: String = ""
    ): String =
        this?.replace(Regex("\\s+"), "") ?: onErrorValue

    fun String.toURI(): URI = URI.create(this)

    fun String.extractEmail(): String? =
        Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}").find(this)?.value

    fun String.htmlToText(): String =
        Jsoup.parse(this).text()

}
