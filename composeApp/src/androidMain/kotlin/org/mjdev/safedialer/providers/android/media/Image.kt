package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

@Suppress("DEPRECATION")
data class Image(
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
        columnName = MediaStore.Images.ImageColumns.DESCRIPTION,
        physicalType = FieldMapping.PhysicalType.String
    )
    var description: String? = null,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.PICASA_ID,
        physicalType = FieldMapping.PhysicalType.String
    )
    var picasaId: String? = null,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.IS_PRIVATE,
        logicalType = FieldMapping.LogicalType.Boolean,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var isPrivate: Boolean = false,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.LATITUDE,
        physicalType = FieldMapping.PhysicalType.Double
    )
    var latitude: Double = 0.0,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.LONGITUDE,
        physicalType = FieldMapping.PhysicalType.Double
    )
    var longitude: Double = 0.0,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.DATE_TAKEN,
        logicalType = FieldMapping.LogicalType.Long,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var dateTaken: Long = 0L,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.ORIENTATION,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var orientation: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var miniThumbMagic: Int = 0,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.BUCKET_ID,
        physicalType = FieldMapping.PhysicalType.String
    )
    var bucketId: String? = null,

    @FieldMapping(
        columnName = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var bucketDisplayName: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        @IgnoreMapping
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI
    }
}
