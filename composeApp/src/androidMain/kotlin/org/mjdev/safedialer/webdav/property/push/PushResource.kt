package org.mjdev.safedialer.webdav.property.push

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser
import java.net.URI
import java.net.URISyntaxException

data class PushResource(
    val uri: URI? = null
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV_PUSH, "push-resource")
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): PushResource =
            PushResource(
                uri = XmlReader(parser).readText()?.let { uri ->
                    try {
                        URI(uri)
                    } catch (_: URISyntaxException) {
                        null
                    }
                }
            )
    }
}
