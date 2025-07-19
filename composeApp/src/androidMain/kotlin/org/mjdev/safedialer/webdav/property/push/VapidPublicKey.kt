package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class VapidPublicKey(
    val type: String? = null,
    val key: String? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "vapid-public-key")
    }

    object Factory : PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): VapidPublicKey {
            return VapidPublicKey(
                type = parser.getAttributeValue(null, "type"),
                key = XmlReader(parser).readText()
            )
        }
    }
}
