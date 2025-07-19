package org.mjdev.safedialer.webdav.webdavlib

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

object XmlUtils {
    private const val FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed"

    private val relaxedFactory =
        XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature(FEATURE_RELAXED, true)
        }

    private val standardFactory: XmlPullParserFactory =
        XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = true
        }

    fun newPullParser(): XmlPullParser =
        try {
            relaxedFactory.newPullParser()
        } catch (_: XmlPullParserException) {
            null
        }
        ?: standardFactory.newPullParser()

    fun newSerializer(): XmlSerializer = standardFactory.newSerializer()

    fun XmlSerializer.insertTag(name: Property.Name, contentGenerator: XmlSerializer.() -> Unit = {}) {
        startTag(name.namespace, name.name)
        contentGenerator(this)
        endTag(name.namespace, name.name)
    }

    fun XmlPullParser.propertyName(): Property.Name {
        val propNs = namespace
        val propName = name
        if (propNs == null || propName == null)
            throw IllegalStateException("Current event must be START_TAG or END_TAG")
        return Property.Name(propNs, propName)
    }
}
