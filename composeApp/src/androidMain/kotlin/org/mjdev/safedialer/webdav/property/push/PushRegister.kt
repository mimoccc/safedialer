package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.HttpUtils
import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.xmlpull.v1.XmlPullParser
import java.time.Instant

data class PushRegister(
    val expires: Instant? = null,
    val subscription: Subscription? = null,
    val trigger: Trigger? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "push-register")
        val EXPIRES = Property.Name(NS_WEBDAV_PUSH, "expires")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): PushRegister {
            var register = PushRegister()
            val depth = parser.depth
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1)
                    when (parser.propertyName()) {
                        EXPIRES ->
                            register = register.copy(
                                expires = XmlReader(parser).readText()?.let {
                                    HttpUtils.parseDate(it)
                                }
                            )
                        Subscription.NAME ->
                            register = register.copy(
                                subscription = Subscription.Factory.create(parser)
                            )
                        Trigger.NAME ->
                            register = register.copy(
                                trigger = Trigger.Factory.create(parser)
                            )
                    }
                eventType = parser.next()
            }
            return register
        }
    }
}
