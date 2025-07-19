package org.mjdev.safedialer.webdav.exception

import org.mjdev.safedialer.webdav.webdavlib.HttpUtils
import okhttp3.Response
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

class ServiceUnavailableException(response: Response) : HttpException(response) {
    private val logger
        get() = Logger.getLogger(javaClass.name)
    val retryAfter: Instant?

    init {
        if (response.code != 503)
            throw IllegalArgumentException("Status code must be 503")
        var retryAfterValue: Instant? = null
        response.header("Retry-After")?.let { after ->
            retryAfterValue = HttpUtils.parseDate(after) ?:
                try {
                    val seconds = after.toLong()
                    Instant.now().plusSeconds(seconds)
                } catch (e: NumberFormatException) {
                    logger.log(Level.WARNING, "Received Retry-After which was not a HTTP-date nor delta-seconds: $after", e)
                    null
                }
        }
        retryAfter = retryAfterValue
    }

    fun getDelayUntil(start: Instant = Instant.now()): Instant {
        if (retryAfter == null)
            return start.plusSeconds(DELAY_UNTIL_DEFAULT)
        return retryAfter.coerceIn(
            minimumValue = start.plusSeconds(DELAY_UNTIL_MIN),
            maximumValue = start.plusSeconds(DELAY_UNTIL_MAX)
        )
    }

    companion object {
        const val DELAY_UNTIL_DEFAULT = 15 * 60L    // 15 min
        const val DELAY_UNTIL_MIN = 1 * 60L         // 1 min
        const val DELAY_UNTIL_MAX = 2 * 60 * 60L    // 2 hours
    }
}
