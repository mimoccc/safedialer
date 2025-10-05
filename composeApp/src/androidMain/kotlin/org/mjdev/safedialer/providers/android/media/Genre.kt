package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

data class Genre(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.GenresColumns.NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var name: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        @IgnoreMapping
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Audio.Genres.INTERNAL_CONTENT_URI
    }
}
