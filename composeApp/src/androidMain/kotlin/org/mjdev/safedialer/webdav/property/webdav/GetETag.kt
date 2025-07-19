package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.QuotedStringUtils
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import okhttp3.Response
import org.xmlpull.v1.XmlPullParser

data class GetETag(
    val rawETag: String?
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "getetag")

        fun fromResponse(response: Response) =
            response.header("ETag")?.let { GetETag(it) }
    }

    val eTag: String?
    var weak: Boolean

    init {
        if (rawETag != null) {
            val tag: String?
            if (rawETag.startsWith("W/")) {
                tag = rawETag.substring(2)
                weak = true
            } else {
                tag = rawETag
                weak = false
            }
            eTag = QuotedStringUtils.decodeQuotedString(tag)
        } else {
            eTag = null
            weak = false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is GetETag)
            return false
        return eTag == other.eTag && weak == other.weak
    }

    override fun hashCode(): Int {
        return eTag.hashCode() xor weak.hashCode()
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): GetETag =
            GetETag(XmlReader(parser).readText())

    }
}
