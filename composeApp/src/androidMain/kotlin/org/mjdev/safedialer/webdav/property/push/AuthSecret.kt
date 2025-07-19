package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class AuthSecret(
    val secret: String? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "auth-secret")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME
        override fun create(parser: XmlPullParser): AuthSecret =
            AuthSecret(XmlReader(parser).readText())
    }
}
