package org.mjdev.safedialer.providers.core

import android.database.Cursor

@Suppress("unused")
class Data<T : Entity>(
    private val cursor: Cursor?,
    private val cls: Class<T>
) {
    fun getList(): List<T> {
        val data = mutableListOf<T>()
        if (cursor == null) return data
        try {
            while (cursor.moveToNext()) {
                val t = Entity.create(cursor, cls)
                if (t != null) data.add(t)
            }
        } finally {
            cursor.close()
        }
        return data
    }

    fun getCursor(): Cursor? = cursor

    fun fromCursor(
        cursor: Cursor
    ): T? = Entity.create(cursor, cls)

    fun fromCursor(
        cursor: Cursor,
        vararg projection: String
    ): T? = Entity.create(cursor, cls, *projection)
}
