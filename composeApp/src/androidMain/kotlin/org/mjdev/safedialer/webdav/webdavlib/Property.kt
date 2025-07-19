package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.exception.InvalidPropertyException
import org.xmlpull.v1.XmlPullParser
import java.io.Serializable
import java.util.LinkedList
import java.util.logging.Level
import java.util.logging.Logger

interface Property {
    data class Name(
        val namespace: String,
        val name: String
    ): Serializable {
        override fun toString() = "$namespace:$name"
    }

    companion object {
        fun parse(parser: XmlPullParser): List<Property> {
            val logger = Logger.getLogger(Property::javaClass.name)
            val depth = parser.depth
            val properties = LinkedList<Property>()
            var eventType = parser.eventType
            while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
                if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                    val depthBeforeParsing = parser.depth
                    val name = Name(parser.namespace, parser.name)
                    try {
                        val property = PropertyRegistry.create(name, parser)
                        assert(parser.depth == depthBeforeParsing)
                        if (property != null) {
                            properties.add(property)
                        } else
                            logger.fine("Ignoring unknown property $name")
                    } catch (e: InvalidPropertyException) {
                        logger.log(Level.WARNING, "Ignoring invalid property", e)
                    }
                }
                eventType = parser.next()
            }
            return properties
        }
    }
}
