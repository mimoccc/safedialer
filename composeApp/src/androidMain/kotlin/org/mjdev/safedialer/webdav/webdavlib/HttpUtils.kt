package org.mjdev.safedialer.webdav.webdavlib

import okhttp3.HttpUrl
import okhttp3.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.logging.Logger
import kotlin.time.Duration.Companion.milliseconds

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

    val httpDateFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .appendPattern("EEE, ")
        .optionalStart()
        .appendPattern("d")
        .optionalEnd()
        .appendPattern(" MMM yyyy HH:mm:ss")
        .appendLiteral(" GMT")
        .toFormatter(Locale.ENGLISH)
        .withZone(ZoneId.of("GMT"))

    fun parseDate(dateStr: String): Instant? = runCatching {
        ZonedDateTime.parse(dateStr, httpDateFormatter).toInstant()
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull()

}
