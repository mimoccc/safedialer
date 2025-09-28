package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.DavResource
import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.mjdev.safedialer.webdav.property.common.HrefListProperty
import org.xmlpull.v1.XmlPullParser

data class Owner(
    val href: String?
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "owner")
    }

    object Factory: HrefListProperty.Factory() {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): Owner =
            Owner(XmlReader(parser).readTextProperty(DavResource.HREF))
    }
}
