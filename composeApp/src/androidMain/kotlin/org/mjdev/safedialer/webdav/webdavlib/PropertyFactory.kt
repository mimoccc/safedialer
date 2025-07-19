package org.mjdev.safedialer.webdav.webdavlib

import org.xmlpull.v1.XmlPullParser

interface PropertyFactory {
    fun getName(): Property.Name
    fun create(parser: XmlPullParser): Property
}
