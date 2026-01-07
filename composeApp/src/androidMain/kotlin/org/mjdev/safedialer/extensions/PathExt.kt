package org.mjdev.safedialer.extensions

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

@Suppress("unused")
object PathExt {

    fun Path.createIfNoExists(): Path = runCatching {
        if (!exists()) createDirectories()
        return this
    }.getOrElse { e ->
        e.printStackTrace()
        this
    }

}
