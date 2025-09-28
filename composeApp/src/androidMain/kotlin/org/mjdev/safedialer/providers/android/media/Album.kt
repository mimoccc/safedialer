package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Album(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.ALBUM,
        physicalType = FieldMapping.PhysicalType.String
    )
    var album: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.ARTIST,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artist: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var numOfSongs: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.FIRST_YEAR,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var firstYear: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.LAST_YEAR,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var lastYear: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.ALBUM_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var albumKey: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AlbumColumns.ALBUM_ART,
        physicalType = FieldMapping.PhysicalType.String
    )
    var albumArt: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Audio.Albums.INTERNAL_CONTENT_URI
    }
}
