package org.mjdev.safedialer.providers.core

import android.database.Cursor

@Suppress("unused")
class Data<T : Entity>(
    private val cursor: Cursor?,
    private val cls: Class<T>,
    private val postProcessor: ((T) -> Unit)? = null
) {
    fun getList(): List<T> {
        val data = mutableListOf<T>()
        if (cursor == null) return data
        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val t = Entity.create<T>(cursor, cls)
                if (t != null) {
                    postProcessor?.invoke(t)
                    data.add(t)
                }
            }
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
