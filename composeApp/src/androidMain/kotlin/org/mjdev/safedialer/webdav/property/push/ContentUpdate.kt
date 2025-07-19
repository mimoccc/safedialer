package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.mjdev.safedialer.webdav.property.webdav.Depth
import org.mjdev.safedialer.webdav.property.webdav.SyncLevel
import org.mjdev.safedialer.webdav.property.webdav.SyncToken
import org.xmlpull.v1.XmlPullParser

data class ContentUpdate(
    val depth: Depth? = null,
    val syncToken: SyncToken? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "content-update")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME
        override fun create(parser: XmlPullParser): ContentUpdate {
            var contentUpdate = ContentUpdate()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    when (parser.propertyName()) {
                        SyncLevel.NAME -> contentUpdate = contentUpdate.copy(
                            depth = Depth.Factory.create(parser)
                        )
                        SyncToken.NAME -> contentUpdate = contentUpdate.copy(
                            syncToken = SyncToken.Factory.create(parser)
                        )
                    }
                }
                eventType = parser.next()
            }
            return contentUpdate
        }
    }
}
