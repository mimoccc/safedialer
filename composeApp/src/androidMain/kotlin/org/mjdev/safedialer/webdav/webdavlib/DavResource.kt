package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.insertTag
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.exception.ConflictException
import org.mjdev.safedialer.webdav.exception.DavException
import org.mjdev.safedialer.webdav.exception.ForbiddenException
import org.mjdev.safedialer.webdav.exception.GoneException
import org.mjdev.safedialer.webdav.exception.HttpException
import org.mjdev.safedialer.webdav.exception.NotFoundException
import org.mjdev.safedialer.webdav.exception.PreconditionFailedException
import org.mjdev.safedialer.webdav.exception.ServiceUnavailableException
import org.mjdev.safedialer.webdav.exception.UnauthorizedException
import org.mjdev.safedialer.webdav.property.webdav.NS_WEBDAV
import org.mjdev.safedialer.webdav.property.webdav.SyncToken
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.EOFException
import java.io.IOException
import java.io.Reader
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.iterator
import kotlin.collections.plusAssign

open class DavResource @JvmOverloads constructor(
    val httpClient: OkHttpClient,
    location: HttpUrl,
    val logger: Logger = Logger.getLogger(DavResource::class.java.name)
) {
    companion object {
        const val MAX_REDIRECTS = 5
        const val HTTP_MULTISTATUS = 207

        val MIME_XML = "application/xml; charset=utf-8".toMediaType()
        val PROPFIND = Property.Name(NS_WEBDAV, "propfind")
        val PROPERTYUPDATE = Property.Name(NS_WEBDAV, "propertyupdate")
        val SET = Property.Name(NS_WEBDAV, "set")
        val REMOVE = Property.Name(NS_WEBDAV, "remove")
        val PROP = Property.Name(NS_WEBDAV, "prop")
        val HREF = Property.Name(NS_WEBDAV, "href")
        val XML_SIGNATURE = "<?xml".toByteArray()

        internal fun createProppatchXml(
            setProperties: Map<Property.Name, String>,
            removeProperties: List<Property.Name>
        ): String {
            val serializer = XmlUtils.newSerializer()
            val writer = StringWriter()
            serializer.setOutput(writer)
            serializer.setPrefix("d", NS_WEBDAV)
            serializer.startDocument("UTF-8", null)
            serializer.insertTag(PROPERTYUPDATE) {
                if (setProperties.isNotEmpty()) {
                    serializer.insertTag(SET) {
                        for (prop in setProperties) {
                            serializer.insertTag(PROP) {
                                serializer.insertTag(prop.key) {
                                    text(prop.value)
                                }
                            }
                        }
                    }
                }
                if (removeProperties.isNotEmpty()) {
                    serializer.insertTag(REMOVE) {
                        for (prop in removeProperties) {
                            insertTag(PROP) {
                                insertTag(prop)
                            }
                        }
                    }
                }
            }
            serializer.endDocument()
            return writer.toString()
        }

    }

    var location: HttpUrl
        private set

    init {
        require(!httpClient.followRedirects) { "httpClient must not follow redirects automatically" }
        this.location = location
    }

    override fun toString() = location.toString()

    fun fileName() = HttpUtils.fileName(location)

    @Throws(IOException::class, HttpException::class)
    fun options(followRedirects: Boolean = false, callback: CapabilitiesCallback) {
        val requestOptions = {
            httpClient.newCall(Request.Builder()
                .method("OPTIONS", null)
                .header("Content-Length", "0")
                .url(location)
                .header("Accept-Encoding", "identity")      // disable compression
                .build()).execute()
        }
        val response = if (followRedirects)
            followRedirects(requestOptions)
        else
            requestOptions()
        response.use {
            checkStatus(response)
            callback.onCapabilities(
                HttpUtils.listHeader(response, "DAV").map { it.trim() }.toSet(),
                response
            )
        }
    }

    @Throws(IOException::class, HttpException::class, DavException::class)
    fun move(destination: HttpUrl, overwrite: Boolean, callback: ResponseCallback) {
        val requestBuilder = Request.Builder()
                .method("MOVE", null)
                .header("Content-Length", "0")
                .header("Destination", destination.toString())
        if (!overwrite)
            requestBuilder.header("Overwrite", "F")
        followRedirects {
            requestBuilder.url(location)
            httpClient.newCall(requestBuilder
                    .build())
                    .execute()
        }.use { response ->
            checkStatus(response)
            if (response.code == HTTP_MULTISTATUS)
                throw HttpException(response)
            location.resolve(response.header("Location") ?: destination.toString())?.let {
                location = it
            }
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class, DavException::class)
    fun copy(destination:HttpUrl, overwrite: Boolean, callback: ResponseCallback) {
        val requestBuilder = Request.Builder()
                .method("COPY", null)
                .header("Content-Length", "0")
                .header("Destination", destination.toString())
        if (!overwrite)
            requestBuilder.header("Overwrite", "F")
        followRedirects {
            requestBuilder.url(location)
            httpClient.newCall(requestBuilder
                    .build())
                    .execute()
        }.use{ response ->
            checkStatus(response)
            if (response.code == HTTP_MULTISTATUS)
                throw HttpException(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class)
    fun mkCol(xmlBody: String?, method: String = "MKCOL", headers: Headers? = null, callback: ResponseCallback) {
        val rqBody = xmlBody?.toRequestBody(MIME_XML)
        val request = Request.Builder()
            .method(method, rqBody)
            .url(UrlUtils.withTrailingSlash(location))
        if (headers != null)
            request.headers(headers)
        followRedirects {
            httpClient.newCall(request.build()).execute()
        }.use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    fun head(callback: ResponseCallback) {
        followRedirects {
            httpClient.newCall(
                Request.Builder()
                    .head()
                    .url(location)
                    .build()
            ).execute()
        }.use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    fun get(accept: String, headers: Headers?): Response =
        followRedirects {
            val request = Request.Builder()
                .get()
                .url(location)
            if (headers != null)
                request.headers(headers)
            request.header("Accept", accept)
            httpClient.newCall(request.build()).execute()
        }

    @Deprecated("Use get(accept, headers, callback) with explicit Accept-Encoding instead")
    @Throws(IOException::class, HttpException::class)
    fun get(accept: String, callback: ResponseCallback) {
        get(accept, Headers.headersOf("Accept-Encoding", "identity"), callback)
    }

    fun get(accept: String, headers: Headers?, callback: ResponseCallback) {
        get(accept, headers).use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class)
    fun getRange(accept: String, offset: Long, size: Int, headers: Headers? = null, callback: ResponseCallback) {
        followRedirects {
            val request = Request.Builder()
                .get()
                .url(location)
            if (headers != null)
                request.headers(headers)
            val lastIndex = offset + size - 1
            request
                .header("Accept", accept)
                .header("Range", "bytes=$offset-$lastIndex")
            httpClient.newCall(request.build()).execute()
        }.use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class)
    fun post(body: RequestBody, ifNoneMatch: Boolean = false, headers: Headers? = null, callback: ResponseCallback) {
        followRedirects {
            val builder = Request.Builder()
                .post(body)
                .url(location)
            if (ifNoneMatch)
                builder.header("If-None-Match", "*")
            if (headers != null)
                builder.headers(headers)
            httpClient.newCall(builder.build()).execute()
        }.use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class)
    fun put(
        body: RequestBody,
        ifETag: String? = null,
        ifScheduleTag: String? = null,
        ifNoneMatch: Boolean = false,
        headers: Map<String, String> = emptyMap(),
        callback: ResponseCallback
    ) {
        followRedirects {
            val builder = Request.Builder()
                    .put(body)
                    .url(location)
            if (ifETag != null)
                builder.header("If-Match", QuotedStringUtils.asQuotedString(ifETag))
            if (ifScheduleTag != null)
                builder.header("If-Schedule-Tag-Match", QuotedStringUtils.asQuotedString(ifScheduleTag))
            if (ifNoneMatch)
                builder.header("If-None-Match", "*")
            for ((key, value) in headers)
                builder.header(key, value)
            httpClient.newCall(builder.build()).execute()
        }.use { response ->
            checkStatus(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class)
    fun delete(
        ifETag: String? = null,
        ifScheduleTag: String? = null,
        headers: Map<String, String> = emptyMap(),
        callback: ResponseCallback
    ) {
        followRedirects {
            val builder = Request.Builder()
                    .delete()
                    .url(location)
            if (ifETag != null)
                builder.header("If-Match", QuotedStringUtils.asQuotedString(ifETag))
            if (ifScheduleTag != null)
                builder.header("If-Schedule-Tag-Match", QuotedStringUtils.asQuotedString(ifScheduleTag))
            for ((key, value) in headers)
                builder.header(key, value)
            httpClient.newCall(builder.build()).execute()
        }.use { response ->
            checkStatus(response)
            if (response.code == HTTP_MULTISTATUS)
                throw HttpException(response)
            callback.onResponse(response)
        }
    }

    @Throws(IOException::class, HttpException::class, DavException::class)
    fun propfind(depth: Int, vararg reqProp: Property.Name, callback: MultiResponseCallback) {
        val serializer = XmlUtils.newSerializer()
        val writer = StringWriter()
        serializer.setOutput(writer)
        serializer.setPrefix("", NS_WEBDAV)
//        serializer.setPrefix("CAL", NS_CALDAV)
//        serializer.setPrefix("CARD", NS_CARDDAV)
        serializer.startDocument("UTF-8", null)
        serializer.insertTag(PROPFIND) {
            insertTag(PROP) {
                for (prop in reqProp)
                    insertTag(prop)
            }
        }
        serializer.endDocument()
        followRedirects {
            httpClient.newCall(Request.Builder()
                    .url(location)
                    .method("PROPFIND", writer.toString().toRequestBody(MIME_XML))
                    .header("Depth", if (depth >= 0) depth.toString() else "infinity")
                    .build()).execute()
        }.use {
            processMultiStatus(it, callback)
        }
    }

    fun proppatch(
        setProperties: Map<Property.Name, String>,
        removeProperties: List<Property.Name>,
        callback: MultiResponseCallback
    ) {
        followRedirects {
            val rqBody = createProppatchXml(setProperties, removeProperties)
            httpClient.newCall(
                Request.Builder()
                    .url(location)
                    .method("PROPPATCH", rqBody.toRequestBody(MIME_XML))
                    .build()
            ).execute()
        }.use {
            processMultiStatus(it, callback)
        }
    }

    fun search(search: String, callback: MultiResponseCallback) {
        followRedirects {
            httpClient.newCall(Request.Builder()
                .url(location)
                .method("SEARCH", search.toRequestBody(MIME_XML))
                .build()).execute()
        }.use {
            processMultiStatus(it, callback)
        }
    }

    protected fun checkStatus(response: Response) {
        if (response.code / 100 == 2)
            return
        throw when (response.code) {
            401 -> UnauthorizedException(response)
            403 -> ForbiddenException(response)
            404 -> NotFoundException(response)
            409 -> ConflictException(response)
            410 -> GoneException(response)
            412 -> PreconditionFailedException(response)
            503 -> ServiceUnavailableException(response)
            else -> HttpException(response)
        }
    }

    internal fun followRedirects(sendRequest: () -> Response): Response {
        lateinit var response: Response
        for (attempt in 1..MAX_REDIRECTS) {
            response = sendRequest()
            if (response.isRedirect)
                response.use {
                    val target = it.header("Location")?.let { location.resolve(it) }
                    if (target != null) {
                        logger.fine("Redirected, new location = $target")
                        if (location.isHttps && !target.isHttps)
                            throw DavException("Received redirect from HTTPS to HTTP")
                        location = target
                    } else
                        throw DavException("Redirected without new Location")
                }
            else
                break
        }
        return response
    }

    fun assertMultiStatus(response: Response) {
        if (response.code != HTTP_MULTISTATUS)
            throw DavException("Expected 207 Multi-Status, got ${response.code} ${response.message}", response = response)
        response.peekBody(XML_SIGNATURE.size.toLong()).use { body ->
            body.contentType()?.let { mimeType ->
                if (((mimeType.type != "application" && mimeType.type != "text")) || mimeType.subtype != "xml") {
                    try {
                        response.peekBody(XML_SIGNATURE.size.toLong()).use { body ->
                            if (XML_SIGNATURE.contentEquals(body.bytes())) {
                                logger.warning("Received 207 Multi-Status that seems to be XML but has MIME type $mimeType")
                                return
                            }
                        }
                    } catch (e: Exception) {
                        logger.log(Level.WARNING, "Couldn't scan for XML signature", e)
                    }
                    throw DavException("Received non-XML 207 Multi-Status", response = response)
                }
            } ?: logger.warning("Received 207 Multi-Status without Content-Type, assuming XML")
        }
    }

    protected fun processMultiStatus(response: Response, callback: MultiResponseCallback): List<Property> {
        checkStatus(response)
        assertMultiStatus(response)
        return response.body.use {
            processMultiStatus(it.charStream(), callback)
        }
    }

    protected fun processMultiStatus(
        reader: Reader,
        callback: MultiResponseCallback
    ): List<Property> {
        val responseProperties = mutableListOf<Property>()
        val parser = XmlUtils.newPullParser()
        fun parseMultiStatus(): List<Property> {
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1)
                    when (parser.propertyName()) {
                        org.mjdev.safedialer.webdav.webdavlib.Response.Companion.RESPONSE ->
                            org.mjdev.safedialer.webdav.webdavlib.Response.Companion.parse(parser, location, callback)
                        SyncToken.NAME ->
                            XmlReader(parser).readText()?.let {
                                responseProperties += SyncToken(it)
                            }
                    }
                eventType = parser.next()
            }
            return responseProperties
        }
        try {
            parser.setInput(reader)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == 1)
                    if (parser.propertyName() == org.mjdev.safedialer.webdav.webdavlib.Response.Companion.MULTISTATUS)
                        return parseMultiStatus()
                eventType = parser.next()
            }
            throw DavException("Multi-Status response didn't contain multistatus XML element")
        } catch (e: EOFException) {
            throw DavException("Incomplete multistatus XML element", e)
        } catch (e: XmlPullParserException) {
            throw DavException("Couldn't parse multistatus XML element", e)
        }
    }
}
