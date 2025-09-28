package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Video(
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
        physicalType = FieldMapping.PhysicalType.Long
    )
    var dateAdded: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.DATE_MODIFIED,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var dateModified: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.MIME_TYPE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mimeType: String? = null,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.WIDTH,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var width: Int = 0,

    @FieldMapping(
        columnName = MediaStore.MediaColumns.HEIGHT,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var height: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.DURATION,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var duration: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.ARTIST,
        physicalType = FieldMapping.PhysicalType.String
    )
    var artist: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.ALBUM,
        physicalType = FieldMapping.PhysicalType.String
    )
    var album: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.RESOLUTION,
        physicalType = FieldMapping.PhysicalType.String
    )
    var resolution: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.DESCRIPTION,
        physicalType = FieldMapping.PhysicalType.String
    )
    var description: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.IS_PRIVATE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var isPrivate: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.TAGS,
        physicalType = FieldMapping.PhysicalType.String
    )
    var tags: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.CATEGORY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var category: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.LANGUAGE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var language: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.LATITUDE,
        physicalType = FieldMapping.PhysicalType.Double
    )
    var latitude: Double = 0.0,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.LONGITUDE,
        physicalType = FieldMapping.PhysicalType.Double
    )
    var longitude: Double = 0.0,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.DATE_TAKEN,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Long
    )
    var dateTaken: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var miniThumbMagic: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.BUCKET_ID,
        physicalType = FieldMapping.PhysicalType.String
    )
    var bucketId: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var bucketDisplayName: String? = null,

    @FieldMapping(
        columnName = MediaStore.Video.VideoColumns.BOOKMARK,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var bookmark: Int = 0
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Video.Media.INTERNAL_CONTENT_URI
    }
}
