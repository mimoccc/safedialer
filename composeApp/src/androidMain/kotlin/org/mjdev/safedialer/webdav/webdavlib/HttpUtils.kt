package org.mjdev.safedialer.webdav.webdavlib

import okhttp3.HttpUrl
import okhttp3.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.logging.Logger

object HttpUtils {
    private const val httpDateFormatStr = "EEE, dd MMM yyyy HH:mm:ss ZZZZ"
    private val httpDateFormat = DateTimeFormatter.ofPattern(httpDateFormatStr, Locale.US)
    private val logger
        get() = Logger.getLogger(javaClass.name)

    fun fileName(url: HttpUrl): String {
        val pathSegments = url.pathSegments.dropLastWhile { it == "" }
        return pathSegments.lastOrNull() ?: ""
    }

    fun listHeader(response: Response, name: String): Array<String> {
        val value = response.headers(name).joinToString(",")
        return value.split(',').filter { it.isNotEmpty() }.toTypedArray()
    }

    fun formatDate(date: Instant): String =
        ZonedDateTime.ofInstant(date, ZoneOffset.UTC).format(httpDateFormat)

    fun parseDate(dateStr: String): Instant? {
        val zonedFormats = arrayOf(
            httpDateFormat,
            DateTimeFormatter.ofPattern("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
        )
        for (format in zonedFormats)
            try {
                return ZonedDateTime.parse(dateStr, format).toInstant()
            } catch (ignored: DateTimeParseException) {
            }
        try {
            val formatC = DateTimeFormatter.ofPattern("EEE MMM ppd HH:mm:ss yyyy", Locale.US)
            val local = LocalDateTime.parse(dateStr, formatC)
            return local.atZone(ZoneOffset.UTC).toInstant()
        } catch (ignored: DateTimeParseException) {
        }
        logger.warning("Couldn't parse HTTP date: $dateStr, ignoring")
        return null
    }
}
