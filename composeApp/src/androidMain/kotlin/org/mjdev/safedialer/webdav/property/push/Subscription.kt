package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class Subscription constructor(
    val webPushSubscription: WebPushSubscription? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "subscription")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): Subscription {
            var webPushSubscription: WebPushSubscription? = null
            XmlReader(parser).processTag(WebPushSubscription.NAME) {
                webPushSubscription = WebPushSubscription.Factory.create(parser)
            }
            return Subscription(webPushSubscription)
        }
    }
}
