package org.mjdev.safedialer.webdav

data class WebDavEntry(
    val name: String,
    val fullUrl: String,
    val isCollection: Boolean
)