package org.mjdev.safedialer.webdav.webdavlib

import okhttp3.Response

fun interface CapabilitiesCallback {
    fun onCapabilities(davCapabilities: Set<String>, response: Response)
}

fun interface MultiResponseCallback {
    fun onResponse(response: org.mjdev.safedialer.webdav.webdavlib.Response, relation:org.mjdev.safedialer.webdav.webdavlib.Response.HrefRelation)
}

fun interface ResponseCallback {
    fun onResponse(response: Response)
}
