package org.mjdev.safedialer.providers.core

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri

@Suppress("SameParameterValue")
abstract class AbstractProvider(
    val context: Context,
    private val contentResolver: ContentResolver = context.contentResolver
) {
    fun registerContentObserver(
        uri: Uri,
        observer: ContentObserver,
        notifyForDescendants: Boolean = false,
    ) {
        contentResolver.registerContentObserver(uri, notifyForDescendants, observer)
    }

    fun unregisterContentObserver(
        observer: ContentObserver
    ) {
        contentResolver.unregisterContentObserver(observer)
    }

    protected open fun <T : Entity> postProcess(
        entity: T
    ) {
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
        return Data(cursor, clazz) { entity -> postProcess(entity) }
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
        return Data(cursor, clazz) { entity -> postProcess(entity) }
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
                t?.let { postProcess(it) }
            }
        } finally {
            cursor.close()
        }
        return t
    }

    protected fun updateTableRow(
        uri: Uri,
        entity: Entity
    ): Int {
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

    abstract fun getUris() : List<Uri>
}
