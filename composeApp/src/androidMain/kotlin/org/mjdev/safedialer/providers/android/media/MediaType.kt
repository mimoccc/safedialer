package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity.CompanionWithUri
import org.mjdev.safedialer.providers.core.EnumInt
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION", "unused")
enum class MediaType(val value: Int) : EnumInt {
    NONE(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE),
    IMAGE(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
    AUDIO(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO),
    VIDEO(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    PLAYLIST(MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST);

    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = Uri.EMPTY

        fun fromInt(
            value: Int
        ): MediaType? = entries.find { it.value == value }
    }
}
