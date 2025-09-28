package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Playlist(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.PlaylistsColumns.NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var name: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.PlaylistsColumns.DATA,
        physicalType = FieldMapping.PhysicalType.Blob
    )
    var data: ByteArray? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.PlaylistsColumns.DATE_ADDED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var dateAdded: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.PlaylistsColumns.DATE_MODIFIED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var dateModified: Long = 0L
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Audio.Playlists.INTERNAL_CONTENT_URI
    }
}
