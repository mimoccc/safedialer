package org.mjdev.safedialer.webdav.exception

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.exception.DavException.Companion.MAX_EXCERPT_SIZE
import okhttp3.MediaType
import okhttp3.Response
import okio.Buffer
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayOutputStream
import java.io.StringReader
import kotlin.math.min
import org.mjdev.safedialer.webdav.webdavlib.Error

internal class HttpResponseInfo private constructor(
    val statusCode: Int,
    val requestExcerpt: String?,
    val responseExcerpt: String?,
    val errors: List<Error>
) {
    companion object {
        fun fromResponse(
//            @WillNotClose
                         response: Response
        ): HttpResponseInfo {
            val request = response.request
            val requestExcerptBuilder = StringBuilder(
                "${request.method} ${request.url}"
            )
            request.body?.let { requestBody ->
                if (requestBody.contentType()?.isText() == true) {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    ByteArrayOutputStream().use { baos ->
                        buffer.writeTo(baos, min(buffer.size, MAX_EXCERPT_SIZE.toLong()))
                        requestExcerptBuilder
                            .append("\n\n")
                            .append(baos.toString())
                    }
                } else
                    requestExcerptBuilder.append("\n\n<request body (${requestBody.contentLength()} bytes)>")
            }
            val mimeType = response.body.contentType()
            val responseBody =
                if (mimeType?.isText() == true)
                    try {
                        response.peekBody(MAX_EXCERPT_SIZE.toLong()).string()
                    } catch (_: Exception) {
                        null
                    }
                else
                    null
            val errors: List<Error> = if (mimeType?.isXml() == true && responseBody != null)
                extractErrors(responseBody)
            else
                emptyList()
            return HttpResponseInfo(
                statusCode = response.code,
                requestExcerpt = requestExcerptBuilder.toString(),
                responseExcerpt = responseBody,
                errors = errors
            )
        }

        private fun extractErrors(xml: String): List<Error> {
            try {
                val parser = XmlUtils.newPullParser()
                parser.setInput(StringReader(xml))
                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.depth == 1)
                        if (parser.propertyName() == Error.NAME)
                            return Error.parseError(parser)
                    eventType = parser.next()
                }
            } catch (_: XmlPullParserException) {
                // Couldn't parse XML, either invalid or maybe it wasn't even XML
            }
            return emptyList()
        }

        private fun MediaType.isText() =
            type == "text" ||
                    (type == "application" && subtype in arrayOf("html", "xml"))

        private fun MediaType.isXml() =
            type in arrayOf("application", "text") && subtype == "xml"
    }
}
