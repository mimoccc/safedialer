package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.property.common.HrefListProperty
import org.xmlpull.v1.XmlPullParser

class GroupMembership(
    override val hrefs: List<String>
): HrefListProperty(hrefs) {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "group-membership")
    }

    object Factory: HrefListProperty.Factory() {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser) = create(parser, ::GroupMembership)
    }
}
