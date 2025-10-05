package org.mjdev.safedialer.providers.android.media

import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

data class File(
    @FieldMapping(columnName = BaseColumns._ID, physicalType = FieldMapping.PhysicalType.Long)
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
        columnName = MediaStore.Files.FileColumns.PARENT,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var parent: Int = 0,
    @FieldMapping(
        columnName = MediaStore.Files.FileColumns.MEDIA_TYPE,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.EnumInt
    )
    var mediaType: MediaType? = null
) : Entity() {
    companion object : CompanionWithUri {
        // todo ?
        @IgnoreMapping
        override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        @IgnoreMapping
        val uriExternal: Uri = MediaStore.Files.getContentUri("external")

        @IgnoreMapping
        val uriInternal: Uri = MediaStore.Files.getContentUri("internal")
    }
}
