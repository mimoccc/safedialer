package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.xmlpull.v1.XmlPullParser

data class WebPushSubscription(
    val pushResource: PushResource? = null,
    val subscriptionPublicKey: SubscriptionPublicKey? = null,
    val authSecret: AuthSecret? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "web-push-subscription")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): WebPushSubscription {
            var subscription = WebPushSubscription()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    when (parser.propertyName()) {
                        PushResource.NAME -> subscription = subscription.copy(pushResource = PushResource.Factory.create(parser))
                        SubscriptionPublicKey.NAME -> subscription = subscription.copy(subscriptionPublicKey = SubscriptionPublicKey.Factory.create(parser))
                        AuthSecret.NAME -> subscription = subscription.copy(authSecret = AuthSecret.Factory.create(parser))
                    }
                }
                eventType = parser.next()
            }
            return subscription
        }
    }
}
