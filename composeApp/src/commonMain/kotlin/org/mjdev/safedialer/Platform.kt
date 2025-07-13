package org.mjdev.safedialer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform