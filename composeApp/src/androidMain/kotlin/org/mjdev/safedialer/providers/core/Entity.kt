package org.mjdev.safedialer.providers.core

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Method

@Suppress("MemberVisibilityCanBePrivate")
abstract class Entity {
    interface CompanionWithUri {
        val uri: Uri
    }

    @Suppress("unused")
    companion object {
        fun <T> getColumns(
            cls: Class<T>
        ): Array<String> {
            val columns = mutableListOf<String>()
            for (field in cls.declaredFields) {
                if (!field.isAnnotationPresent(IgnoreMapping::class.java)) {
                    val contentField = field.getAnnotation(FieldMapping::class.java) ?: continue
                    columns.add(contentField.columnName)
                }
            }
            return columns.toTypedArray()
        }

        fun <T> getWriteColumns(
            cls: Class<T>
        ): Array<String> {
            val columns = mutableListOf<String>()
            for (field in cls.declaredFields) {
                if (!field.isAnnotationPresent(IgnoreMapping::class.java)) {
                    val contentField = field.getAnnotation(FieldMapping::class.java)
                    if (contentField?.canUpdate == true) {
                        columns.add(contentField.columnName)
                    }
                }
            }
            return columns.toTypedArray()
        }

        fun getId(
            entity: Entity
        ): Long = getColumnValue("_id", entity)?.toString()?.toLongOrNull() ?: 0L

        fun getColumnValue(
            columnName: String,
            entity: Entity
        ): Any? = getColumnField(columnName, entity)?.get(entity)

        fun getColumnValue(
            field: Field,
            entity: Entity
        ): Any? = field.get(entity)

        fun getColumnField(
            columnName: String,
            entity: Entity
        ): Field? = entity.javaClass.declaredFields.firstOrNull { field ->
            !field.isAnnotationPresent(IgnoreMapping::class.java) &&
                    field.getAnnotation(FieldMapping::class.java)?.columnName == columnName
        }

        fun getContentValues(
            columns: Array<String>,
            entity: Entity
        ): ContentValues {
            val contentValues = ContentValues()
            for (column in columns) {
                val field = getColumnField(column, entity)
                val fieldMapping = field?.getAnnotation(FieldMapping::class.java)
                val value = getColumnValue(field!!, entity)
                fieldMapping?.let {
                    when (it.physicalType) {
                        FieldMapping.PhysicalType.Int -> contentValues.put(
                            column,
                            value.toString().toIntOrNull()
                        )

                        FieldMapping.PhysicalType.String -> contentValues.put(
                            column,
                            value.toString()
                        )

                        FieldMapping.PhysicalType.Long -> contentValues.put(
                            column,
                            value.toString().toLongOrNull()
                        )

                        FieldMapping.PhysicalType.Double -> contentValues.put(
                            column,
                            value.toString().toDoubleOrNull()
                        )

                        // todo, base 64 string ?
                        FieldMapping.PhysicalType.Blob -> {
                            contentValues.put(
                                column,
                                value.toString()
                            )
                        }
                    }
                }
            }
            return contentValues
        }

        fun getFlattenedValues(
            columns: Array<String>,
            entity: Entity
        ): List<Any?> = columns.map { column ->
            val field = getColumnField(column, entity)
            val value = getColumnValue(field!!, entity)
            if (value is EnumInt) value.toString() else value
        }

        @Suppress("DEPRECATION")
        fun <T : Entity> create(
            cursor: Cursor,
            cls: Class<T>
        ): T? = try {
            val entity: T = cls.newInstance()
            val fields = cls.declaredFields
            create(entity, cursor, fields)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        @Suppress("DEPRECATION")
        fun <T : Entity> create(
            cursor: Cursor,
            cls: Class<T>,
            vararg projection: String
        ): T? = try {
            val entity: T = cls.newInstance()
            val fields = Array(projection.size) { i ->
                getColumnField(projection[i], entity)!!
            }
            create(entity, cursor, fields)
        } catch (e: Exception) {
            e.printStackTrace(); null
        }

        fun <T : Entity> create(
            entity: T,
            cursor: Cursor,
            fields: Array<Field>
        ): T {
            for (field in fields) {
                try {
                    val contentField = field.getAnnotation(FieldMapping::class.java)
                    val ignoreMapping = field.getAnnotation(IgnoreMapping::class.java)
                    if (contentField != null && ignoreMapping == null) {
                        val columnName = contentField.columnName
                        val methodName = "get" + contentField.physicalType.name
                        val method: Method = Cursor::class.java.getDeclaredMethod(
                            methodName,
                            Int::class.javaPrimitiveType
                        )
                        val columnIndex = cursor.getColumnIndexOrThrow(columnName)
                        val obj = method.invoke(cursor, columnIndex)
                        field.isAccessible = true
                        when (contentField.logicalType) {
                            FieldMapping.LogicalType.Boolean -> field.setBoolean(
                                entity,
                                (obj?.toString()?.toInt() ?: 0) != 0
                            )

                            FieldMapping.LogicalType.EnumInt -> {
                                val companion = field.type.getDeclaredField("Companion").let {
                                    it.isAccessible = true
                                    it.get(null)
                                }
                                val fromIntMethod = companion.javaClass.getMethod(
                                    "fromInt",
                                    Int::class.javaPrimitiveType
                                )
                                val enumInstance = fromIntMethod.invoke(companion, obj)
                                field.isAccessible = true
                                field.set(entity, enumInstance)
                            }

                            FieldMapping.LogicalType.Array -> {
                                val strings = obj?.toString()
                                    ?.split(contentField.splitRegex.toRegex())
                                    ?.toTypedArray() ?: emptyArray()
                                field.isAccessible = true
                                field.set(entity, strings)
                            }

                            else -> {
                                field.isAccessible = true
                                field.set(entity, obj)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(Entity::class.java.name, "field=" + field.name, e)
                }
            }
            return entity
        }

        fun Boolean.toInt(): Int = if (this) 1 else 0
    }

    override fun toString(): String = StringBuilder().apply {
        append("{")
        this@Entity.javaClass.declaredFields.forEachIndexed { idx, field ->
            runCatching {
                if (idx > 0) append(", ")
                field.isAccessible = true
                append(field.name).append("=").append(field.get(this))
            }
        }
        append("}")
    }.toString()
}
