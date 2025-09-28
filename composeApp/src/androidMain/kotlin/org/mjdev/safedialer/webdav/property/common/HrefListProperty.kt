package org.mjdev.safedialer.webdav.property.common

import org.mjdev.safedialer.webdav.webdavlib.DavResource
import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

abstract class HrefListProperty(
    open val hrefs: List<String>
) : Property {
    abstract class Factory : PropertyFactory {
        @Deprecated("hrefs is no longer mutable.", level = DeprecationLevel.ERROR)
        fun create(parser: XmlPullParser, list: HrefListProperty): HrefListProperty {
            val hrefs = list.hrefs.toMutableList()
            XmlReader(parser).readTextPropertyList(DavResource.HREF, hrefs)
            return list
        }

        fun <PropertyType> create(
            parser: XmlPullParser,
            constructor: (
                hrefs: List<String>
            ) -> PropertyType
        ): PropertyType {
            val hrefs = mutableListOf<String>()
            XmlReader(parser).readTextPropertyList(DavResource.HREF, hrefs)
            return constructor(hrefs)
        }
    }
}
