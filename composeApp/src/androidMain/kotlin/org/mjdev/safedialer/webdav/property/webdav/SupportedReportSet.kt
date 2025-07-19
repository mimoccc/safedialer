package org.mjdev.safedialer.webdav.property.webdav

import org.mjdev.safedialer.webdav.webdavlib.Property
import org.mjdev.safedialer.webdav.webdavlib.PropertyFactory
import org.mjdev.safedialer.webdav.webdavlib.XmlReader
import org.xmlpull.v1.XmlPullParser

data class SupportedReportSet(
    val reports: Set<String> = emptySet()
): Property {
    companion object {
        @JvmField
        val NAME = Property.Name(NS_WEBDAV, "supported-report-set")
        val SUPPORTED_REPORT = Property.Name(NS_WEBDAV, "supported-report")
        val REPORT = Property.Name(NS_WEBDAV, "report")
        const val SYNC_COLLECTION = "DAV:sync-collection"    // collection synchronization (RFC 6578)
    }

    object Factory: PropertyFactory {
        override fun getName() = NAME

        override fun create(parser: XmlPullParser): SupportedReportSet {
            val reports = mutableSetOf<String>()
            XmlReader(parser).processTag(SUPPORTED_REPORT) {
                processTag(REPORT) {
                    parser.nextTag()
                    if (parser.eventType == XmlPullParser.TEXT)
                        reports += parser.text
                    else if (parser.eventType == XmlPullParser.START_TAG)
                        reports += "${parser.namespace}${parser.name}"
                }
            }
            return SupportedReportSet(reports)
        }
    }
}
