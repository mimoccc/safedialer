package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Audio(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.DATA,
        physicalType = FieldMapping.PhysicalType.Blob
    )
    var data: ByteArray? = null,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.SIZE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var size: Int = 0,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.DISPLAY_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var displayName: String? = null,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.TITLE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var title: String? = null,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.DATE_ADDED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var dateAdded: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.DATE_MODIFIED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var dateModified: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.MIME_TYPE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mimeType: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.TITLE_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var titleKey: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.DURATION,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var duration: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.BOOKMARK,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var bookmark: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ARTIST_ID,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var artistId: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ARTIST,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artist: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ARTIST_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artistKey: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.COMPOSER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var composer: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ALBUM_ID,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var albumId: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ALBUM,
        physicalType = FieldMapping.PhysicalType.String
    )
    var album: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.ALBUM_KEY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var albumKey: String? = null,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.TRACK,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var track: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.YEAR,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var year: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.IS_MUSIC,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isMusic: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.IS_PODCAST,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isPodcast: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.IS_RINGTONE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isRingtone: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.IS_ALARM,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isAlarm: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Audio.AudioColumns.IS_NOTIFICATION,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isNotification: Boolean = false
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        @IgnoreMapping
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
    }
}
