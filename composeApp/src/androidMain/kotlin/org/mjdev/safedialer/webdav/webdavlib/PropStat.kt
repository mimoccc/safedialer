package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.property.webdav.NS_WEBDAV
import okhttp3.Protocol
import okhttp3.internal.http.StatusLine
import org.xmlpull.v1.XmlPullParser
import java.net.ProtocolException
import java.util.LinkedList

data class PropStat(
        val properties: List<Property>,
        val status: StatusLine,
        val error: List<Error>? = null
) {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "propstat")

        private val ASSUMING_OK = StatusLine(Protocol.HTTP_1_1, 200, "Assuming OK")
        private val INVALID_STATUS = StatusLine(Protocol.HTTP_1_1, 500, "Invalid status line")

        fun parse(parser: XmlPullParser): PropStat {
            val depth = parser.depth
            var status: StatusLine? = null
            val prop = LinkedList<Property>()
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1)
                    when (parser.propertyName()) {
                        DavResource.PROP ->
                            prop.addAll(Property.parse(parser))
                        Response.Companion.STATUS ->
                            status = try {
                                StatusLine.parse(parser.nextText())
                            } catch (e: ProtocolException) {
                                INVALID_STATUS
                            }
                    }
                eventType = parser.next()
            }
            return PropStat(prop, status ?: ASSUMING_OK)
        }
    }

    fun isSuccess() = status.code/100 == 2
}
