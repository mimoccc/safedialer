package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser
import java.time.Instant

data class GetLastModified(
    val lastModified: Instant?
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "getlastmodified")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): GetLastModified {
            // <!ELEMENT getlastmodified (#PCDATA) >
            return GetLastModified(
                XmlReader(parser).readHttpDate()
            )
        }
    }
}
