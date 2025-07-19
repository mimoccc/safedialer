package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class Depth(
    val depth: Int? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "depth")
        const val INFINITY = Int.MAX_VALUE
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): Depth {
            val text = XmlReader(parser).readText()
            val level = if (text.equals("infinity", true))
                INFINITY
            else
                text?.toIntOrNull()
            return Depth(level)
        }
    }
}
