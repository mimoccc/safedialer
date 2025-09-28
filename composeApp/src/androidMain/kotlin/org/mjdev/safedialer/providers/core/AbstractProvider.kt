package org.mjdev.safedialer.providers.core

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri

@Suppress("SENSELESS_COMPARISON", "SENSELESS_COMPARISON")
abstract class AbstractProvider(
    val context: Context
) {
    val contentResolver: ContentResolver = context.contentResolver

    fun registerContentObserver(
        uri: Uri,
        observer: ContentObserver
    ) {
        contentResolver.registerContentObserver(uri, false, observer)
    }

    protected fun <T : Entity> getContentTableData(
        uri: Uri,
        clazz: Class<T>
    ): Data<T>? {
        val cursor = contentResolver.query(
            uri,
            Entity.getColumns(clazz),
            null,
            null,
            null
        ) ?: return null
        return Data(cursor, clazz)
    }

    protected fun <T : Entity> getContentTableData(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        clazz: Class<T>
    ): Data<T>? {
        val cursor = contentResolver.query(
            uri,
            Entity.getColumns(clazz),
            selection,
            selectionArgs,
            sortOrder
        )
        if (cursor == null) {
            return null
        }
        return Data(cursor, clazz)
    }

    protected fun <T : Entity> getContentRowData(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        clazz: Class<T>
    ): T? {
        var t: T? = null
        val cursor = contentResolver.query(
            uri,
            Entity.getColumns(clazz),
            selection,
            selectionArgs,
            sortOrder
        )
        if (cursor == null) {
            return null
        }
        try {
            if (cursor.moveToNext()) {
                t = Entity.create(cursor, clazz)
            }
        } finally {
            cursor.close()
        }
        return t
    }

    protected fun updateTableRow(uri: Uri, entity: Entity): Int {
        val id = Entity.getId(entity)
        val columns = Entity.getWriteColumns(entity.javaClass)
        if (columns.isNotEmpty()) {
            val values = Entity.getContentValues(columns, entity)
            val updateUri = ContentUris.withAppendedId(uri, id)
            val rows = contentResolver.update(updateUri, values, null, null)
            return rows
        } else {
            return 0
        }
    }
}
