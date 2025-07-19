package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.property.webdav.NS_WEBDAV
import org.xmlpull.v1.XmlPullParser
import java.io.Serializable

data class Error(
    val name: Property.Name
): Serializable {
    companion object {
        val NAME = Property.Name(NS_WEBDAV, "error")
        fun parseError(parser: XmlPullParser): List<Error> {
            val names = mutableSetOf<Property.Name>()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1)
                    names += Property.Name(parser.namespace, parser.name)
                eventType = parser.next()
            }
            return names.map { Error(it) }
        }
        val NEED_PRIVILEGES = Error(Property.Name(NS_WEBDAV, "need-privileges"))
        val VALID_SYNC_TOKEN = Error(Property.Name(NS_WEBDAV, "valid-sync-token"))
    }

    override fun equals(other: Any?) =
            (other is Error) && other.name == name

    override fun hashCode() = name.hashCode()
}
