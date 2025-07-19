package org.mjdev.safedialer.webdav.webdavlib

import okhttp3.HttpUrl

object UrlUtils {
    fun hostToDomain(host: String?): String? {
        if (host == null)
            return null
        val withoutTrailingDot = host.removeSuffix(".")
        val labels = withoutTrailingDot.split('.')
        return if (labels.size >= 2) {
            labels[labels.size - 2] + "." + labels[labels.size - 1]
        } else
            withoutTrailingDot
    }

    fun omitTrailingSlash(url: HttpUrl): HttpUrl {
        val idxLast = url.pathSize - 1
        val hasTrailingSlash = url.pathSegments[idxLast] == ""
        return if (hasTrailingSlash)
            url.newBuilder().removePathSegment(idxLast).build()
        else
            url
    }

    fun withTrailingSlash(url: HttpUrl): HttpUrl {
        val idxLast = url.pathSize - 1
        val hasTrailingSlash = url.pathSegments[idxLast] == ""
        return if (hasTrailingSlash)
            url
        else
            url.newBuilder().addPathSegment("").build()
    }
}

fun HttpUrl.equalsForWebDAV(other: HttpUrl): Boolean {
    if (this == other)
        return true
    val uri1 = toUri()
    val uri2 = other.toUri()
    return uri1.scheme.equals(uri2.scheme, true) && uri1.schemeSpecificPart == uri2.schemeSpecificPart
}
