package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.xmlpull.v1.XmlPullParser

class PushTransports private constructor(
    val transports: Set<PushTransport>
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "transports")
    }

    fun hasWebPush() = transports.any { it is WebPush }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): PushTransports {
            val transports = mutableListOf<PushTransport>()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    when (parser.propertyName()) {
                        WebPush.NAME -> transports += WebPush.Factory.create(parser)
                    }
                }
                eventType = parser.next()
            }
            return PushTransports(transports.toSet())
        }
    }
}
