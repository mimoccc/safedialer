package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class SyncToken(
    val token: String?
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "sync-token")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser) =
            SyncToken(XmlReader(parser).readText())

    }
}
