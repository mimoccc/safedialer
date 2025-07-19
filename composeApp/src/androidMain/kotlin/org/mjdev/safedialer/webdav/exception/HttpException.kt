package org.mjdev.safedialer.webdav.exception

import org.mjdev.safedialer.webdav.webdavlib.Error
import okhttp3.Response

open class HttpException(
    message: String? = null,
    cause: Throwable? = null,
    override val statusCode: Int,
    requestExcerpt: String?,
    responseExcerpt: String?,
    errors: List<Error> = emptyList()
): DavException(message, cause, statusCode, requestExcerpt, responseExcerpt, errors) {
    constructor(
        response: Response,
        message: String = "HTTP ${response.code} ${response.message}",
        cause: Throwable? = null
    ) : this(HttpResponseInfo.fromResponse(response), message, cause)

    private constructor(
        httpResponseInfo: HttpResponseInfo,
        message: String?,
        cause: Throwable? = null
    ): this(
        message = message,
        cause = cause,
        statusCode = httpResponseInfo.statusCode,
        requestExcerpt = httpResponseInfo.requestExcerpt,
        responseExcerpt = httpResponseInfo.responseExcerpt,
        errors = httpResponseInfo.errors
    )

    val isRedirect
        get() = statusCode / 100 == 3

    val isClientError
        get() = statusCode / 100 == 4

    val isServerError
        get() = statusCode / 100 == 5
}
