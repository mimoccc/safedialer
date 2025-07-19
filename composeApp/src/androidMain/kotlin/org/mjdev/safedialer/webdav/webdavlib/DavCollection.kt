package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.webdavlib.XmlUtils.insertTag
import org.mjdev.safedialer.webdav.property.webdav.NS_WEBDAV
import org.mjdev.safedialer.webdav.property.webdav.SyncToken
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.StringWriter
import java.util.logging.Logger

open class DavCollection @JvmOverloads constructor(
    httpClient: OkHttpClient,
    location: HttpUrl,
    logger: Logger = Logger.getLogger(DavCollection::class.java.name)
): DavResource(httpClient, location, logger) {
    companion object {
        val SYNC_COLLECTION = Property.Name(NS_WEBDAV, "sync-collection")
        val SYNC_LEVEL = Property.Name(NS_WEBDAV, "sync-level")
        val LIMIT = Property.Name(NS_WEBDAV, "limit")
        val NRESULTS = Property.Name(NS_WEBDAV, "nresults")
    }

    fun reportChanges(syncToken: String?, infiniteDepth: Boolean, limit: Int?, vararg properties: Property.Name, callback: MultiResponseCallback): List<Property> {
        val serializer = XmlUtils.newSerializer()
        val writer = StringWriter()
        serializer.setOutput(writer)
        serializer.startDocument("UTF-8", null)
        serializer.setPrefix("", NS_WEBDAV)
        serializer.insertTag(SYNC_COLLECTION) {
            insertTag(SyncToken.NAME) {
                if (syncToken != null)
                    text(syncToken)
            }
            insertTag(SYNC_LEVEL) {
                text(if (infiniteDepth) "infinite" else "1")
            }
            if (limit != null)
                insertTag(LIMIT) {
                    insertTag(NRESULTS) {
                        text(limit.toString())
                    }
                }
            insertTag(PROP) {
                for (prop in properties)
                    insertTag(prop)
            }
        }
        serializer.endDocument()
        followRedirects {
            httpClient.newCall(Request.Builder()
                    .url(location)
                    .method("REPORT", writer.toString().toRequestBody(MIME_XML))
                    .header("Depth", "0")
                    .build()).execute()
        }.use {
            return processMultiStatus(it, callback)
        }
    }
}
