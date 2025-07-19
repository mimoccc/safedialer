package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class GetContentLength(
    val contentLength: Long?
) : Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "getcontentlength")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser) =
            GetContentLength(XmlReader(parser).readLong())
    }
}
