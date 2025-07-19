package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.xmlpull.v1.XmlPullParser

data class PushMessage(
    val topic: Topic? = null,
    val contentUpdate: ContentUpdate? = null,
    val propertyUpdate: PropertyUpdate? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "push-message")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): PushMessage {
            var message = PushMessage()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    when (parser.propertyName()) {
                        Topic.NAME -> message = message.copy(
                            topic = Topic.Factory.create(parser)
                        )
                        ContentUpdate.NAME -> message = message.copy(
                            contentUpdate = ContentUpdate.Factory.create(parser)
                        )
                        PropertyUpdate.NAME -> message = message.copy(
                            propertyUpdate = PropertyUpdate.Factory.create(parser)
                        )
                    }
                }
                eventType = parser.next()
            }
            return message
        }
    }
}
