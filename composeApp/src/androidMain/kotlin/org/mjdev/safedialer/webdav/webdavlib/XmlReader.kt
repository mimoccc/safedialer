package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.propertyName
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

class XmlReader(
    private val parser: XmlPullParser
) {
    @Throws(IOException::class, XmlPullParserException::class)
    fun processTag(name: Property.Name, processor: XmlReader.() -> Unit) {
        val depth = parser.depth
        var eventType = parser.eventType
        while (!((eventType == XmlPullParser.END_TAG || eventType == XmlPullParser.END_DOCUMENT) && parser.depth == depth)) {
            if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1 && parser.propertyName() == name)
                processor()
            eventType = parser.next()
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun readText(): String? {
        var text: String? = null
        val depth = parser.depth
        var eventType = parser.eventType
        while (!(eventType == XmlPullParser.END_TAG && parser.depth == depth)) {
            if (eventType == XmlPullParser.TEXT && parser.depth == depth)
                text = parser.text
            eventType = parser.next()
        }
        return text
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun readTextProperty(name: Property.Name): String? {
        var result: String? = null
        val depth = parser.depth
        var eventType = parser.eventType
        while (!((eventType == XmlPullParser.END_TAG || eventType == XmlPullParser.END_DOCUMENT) && parser.depth == depth)) {
            if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1 && parser.propertyName() == name && result == null)
                result = parser.nextText()
            eventType = parser.next()
        }
        return result
    }

    @Throws(IOException::class, XmlPullParserException::class)
    fun readTextPropertyList(name: Property.Name, list: MutableCollection<String>) {
        val depth = parser.depth
        var eventType = parser.eventType
        while (!((eventType == XmlPullParser.END_TAG || eventType == XmlPullParser.END_DOCUMENT) && parser.depth == depth)) {
            if (eventType == XmlPullParser.START_TAG && parser.depth == depth + 1 && parser.propertyName() == name)
                list.add(parser.nextText())
            eventType = parser.next()
        }
    }

    fun readHttpDate(): Instant? {
        return readText()?.let { rawDate ->
            val date = HttpUtils.parseDate(rawDate)
            if (date != null)
                date
            else {
                val logger = Logger.getLogger(javaClass.name)
                logger.warning("Couldn't parse HTTP-date")
                null
            }
        }
    }

    fun readLong(): Long? {
        return readText()?.let { valueStr ->
            try {
                valueStr.toLong()
            } catch(e: NumberFormatException) {
                val logger = Logger.getLogger(javaClass.name)
                logger.log(Level.WARNING, "Couldn't parse as Long: $valueStr", e)
                null
            }
        }
    }

//    fun readContentTypes(tagName: Property.Name, onNewType: (MediaType) -> Unit) {
//        try {
//            processTag(tagName) {
//                parser.getAttributeValue(null, CONTENT_TYPE)?.let { contentType ->
//                    var type = contentType
//                    parser.getAttributeValue(null, VERSION)?.let { version -> type += "; version=$version" }
//                    type.toMediaTypeOrNull()?.let(onNewType)
//                }
//            }
//        } catch(e: XmlPullParserException) {
//            val logger = Logger.getLogger(javaClass.name)
//            logger.log(Level.SEVERE, "Couldn't parse content types", e)
//        }
//    }
}
