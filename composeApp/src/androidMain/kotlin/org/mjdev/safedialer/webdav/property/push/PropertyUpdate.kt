package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.property.webdav.SyncLevel
import org.xmlpull.v1.XmlPullParser

data class PropertyUpdate(
    val syncLevel: SyncLevel? = null,
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "property-update")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): PropertyUpdate {
            var propertyUpdate = PropertyUpdate()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    when (parser.propertyName()) {
                        SyncLevel.NAME -> propertyUpdate = propertyUpdate.copy(
                            syncLevel = SyncLevel.Factory.create(parser)
                        )
                    }
                }
                eventType = parser.next()
            }
            return propertyUpdate
        }
    }
}
