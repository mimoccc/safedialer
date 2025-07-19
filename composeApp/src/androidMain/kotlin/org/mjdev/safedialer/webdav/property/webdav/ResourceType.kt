package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.xmlpull.v1.XmlPullParser

class ResourceType(
    val types: Set<Property.Name> = emptySet()
) : Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "resourcetype")
        val COLLECTION = Property.Name(NS_WEBDAV, "collection")
        val PRINCIPAL = Property.Name(NS_WEBDAV, "principal")
    }

    object Factory : PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): ResourceType {
            val types = mutableSetOf<Property.Name>()

            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    var typeName = Property.Name(parser.namespace, parser.name)
                    when (typeName) {       // if equals(), replace by our instance
                        COLLECTION -> typeName = COLLECTION
                        PRINCIPAL -> typeName = PRINCIPAL
//                        ADDRESSBOOK -> typeName = ADDRESSBOOK
//                        CALENDAR -> typeName = CALENDAR
//                        CALENDAR_PROXY_READ -> typeName = CALENDAR_PROXY_READ
//                        CALENDAR_PROXY_WRITE -> typeName = CALENDAR_PROXY_WRITE
//                        SUBSCRIBED -> typeName = SUBSCRIBED
                    }
                    types.add(typeName)
                }
                eventType = parser.next()
            }
            assert(parser.depth == depth)
            return ResourceType(types)
        }
    }
}
