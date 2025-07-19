package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class SyncLevel(
    val level: Int? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "sync-level")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): SyncLevel {
            val text = XmlReader(parser).readText()
            val level = if (text == "infinite") Int.MAX_VALUE else text?.toIntOrNull()
            return SyncLevel(level)
        }
    }
}
