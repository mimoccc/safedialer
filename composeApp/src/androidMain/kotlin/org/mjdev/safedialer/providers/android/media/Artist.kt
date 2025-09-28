package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Artist(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.ArtistColumns.ARTIST,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artist: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.ArtistColumns.ARTIST_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artistKey: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var numOfAlbums: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var numOfTracks: Int = 0
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Audio.Artists.INTERNAL_CONTENT_URI
    }
}
