package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class QuotaUsedBytes(
    val quotaUsedBytes: Long?
) : Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "quota-used-bytes")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser) =
            QuotaUsedBytes(XmlReader(parser).readLong())
    }
}
