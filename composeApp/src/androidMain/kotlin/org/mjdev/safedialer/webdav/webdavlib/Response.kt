package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.property.webdav.NS_WEBDAV
import org.mjdev.safedialer.webdav.property.webdav.ResourceType
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Protocol
import okhttp3.internal.http.StatusLine
import org.xmlpull.v1.XmlPullParser
import java.net.ProtocolException
import java.util.logging.Logger

@Suppress("unused")
data class Response(
    val requestedUrl: HttpUrl,
    val href: HttpUrl,
    val status: StatusLine?,
    val propstat: List<PropStat>,
    val error: List<Error>? = null,
    val newLocation: HttpUrl? = null
) {
    enum class HrefRelation {
        SELF, MEMBER, OTHER
    }

    val properties: List<Property> by lazy {
        if (isSuccess())
            propstat.filter { it.isSuccess() }.map { it.properties }.flatten()
        else
            emptyList()
    }

    operator fun<T: Property> get(clazz: Class<T>) =
            properties.filterIsInstance(clazz).firstOrNull()

    fun isSuccess() = status == null || status.code/100 == 2

    fun hrefName() = HttpUtils.fileName(href)

    companion object {
        val RESPONSE = Property.Name(NS_WEBDAV, "response")
        val MULTISTATUS = Property.Name(NS_WEBDAV, "multistatus")
        val STATUS = Property.Name(NS_WEBDAV, "status")
        val LOCATION = Property.Name(NS_WEBDAV, "location")

        fun parse(parser: XmlPullParser, location: HttpUrl, callback: MultiResponseCallback) {
            val logger = Logger.getLogger(Response::javaClass.name)
            val depth = parser.depth
            var hrefOrNull: HttpUrl? = null
            var status: StatusLine? = null
            val propStat = mutableListOf<PropStat>()
            var error: List<Error>? = null
            var newLocation: HttpUrl? = null
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth+1)
                    when (parser.propertyName()) {
                        DavResource.HREF -> {
                            var sHref = parser.nextText()
                            if (!sHref.startsWith("/")) {
                                val firstColon = sHref.indexOf(':')
                                if (firstColon != -1) {
                                    var hierarchical = false
                                    try {
                                        if (sHref.substring(firstColon, firstColon + 3) == "://")
                                            hierarchical = true
                                    } catch (e: IndexOutOfBoundsException) {
                                    }
                                    if (!hierarchical)
                                        sHref = "./$sHref"
                                }
                            }
                            hrefOrNull = location.resolve(sHref)
                        }
                        STATUS ->
                            status = try {
                                StatusLine.parse(parser.nextText())
                            } catch(e: ProtocolException) {
                                logger.warning("Invalid status line, treating as HTTP error 500")
                                StatusLine(Protocol.HTTP_1_1, 500, "Invalid status line")
                            }
                        PropStat.NAME ->
                            PropStat.parse(parser).let { propStat += it }
                        Error.NAME ->
                            error = Error.parseError(parser)
                        LOCATION ->
                            newLocation = parser.nextText().toHttpUrlOrNull()
                        }
                eventType = parser.next()
            }
            if (hrefOrNull == null) {
                logger.warning("Ignoring XML response element without valid href")
                return
            }
            var href: HttpUrl = hrefOrNull
            propStat.filter { it.isSuccess() }
                .map { it.properties }
                .filterIsInstance<ResourceType>()
                .firstOrNull()
                ?.let { type ->
                    if (type.types.contains(ResourceType.COLLECTION))
                        href = UrlUtils.withTrailingSlash(href)
                }
            val relation = when {
                UrlUtils.omitTrailingSlash(href).equalsForWebDAV(UrlUtils.omitTrailingSlash(location)) ->
                    HrefRelation.SELF
                else -> {
                    if (location.scheme == href.scheme && location.host == href.host && location.port == href.port) {
                        val locationSegments = location.pathSegments
                        val hrefSegments = href.pathSegments
                        var nBasePathSegments = locationSegments.size
                        if (locationSegments[nBasePathSegments - 1] == "")
                            nBasePathSegments--
                        var relation = HrefRelation.OTHER
                        if (hrefSegments.size > nBasePathSegments) {
                            val sameBasePath = (0 until nBasePathSegments).none { locationSegments[it] != hrefSegments[it] }
                            if (sameBasePath)
                                relation = HrefRelation.MEMBER
                        }
                        relation
                    } else
                        HrefRelation.OTHER
                }
            }
            callback.onResponse(
                Response(
                    requestedUrl = location,
                    href = href,
                    status = status,
                    propstat = propStat,
                    error = error,
                    newLocation = newLocation
                ),
                relation
            )
        }
    }
}
