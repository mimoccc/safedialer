package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.xmlpull.v1.XmlPullParser

data class GetContentType(
    val type: MediaType?
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "getcontenttype")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser) =
            GetContentType(XmlReader(parser).readText()?.toMediaTypeOrNull())
    }
}
