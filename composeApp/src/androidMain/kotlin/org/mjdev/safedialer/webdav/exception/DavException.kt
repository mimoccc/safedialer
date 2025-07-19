package org.mjdev.safedialer.webdav.exception

import org.mjdev.safedialer.webdav.webdavlib.Error
import okhttp3.Response

open class DavException(
    message: String? = null,
    cause: Throwable? = null,
    open val statusCode: Int? = null,
    val requestExcerpt: String? = null,
    val responseExcerpt: String? = null,
    val errors: List<Error> = emptyList()
): Exception(message, cause) {
    constructor(
        message: String,
        cause: Throwable? = null,
//        @WillNotClose
        response: Response
    ) : this(message, cause, HttpResponseInfo.fromResponse(response))

    private constructor(
        message: String?,
        cause: Throwable? = null,
        httpResponseInfo: HttpResponseInfo
    ): this(
        message = message,
        cause = cause,
        statusCode = httpResponseInfo.statusCode,
        requestExcerpt = httpResponseInfo.requestExcerpt,
        responseExcerpt = httpResponseInfo.responseExcerpt,
        errors = httpResponseInfo.errors
    )

    companion object {
        const val MAX_EXCERPT_SIZE = 20*1024
    }
}
