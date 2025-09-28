package org.mjdev.safedialer.helpers

import android.net.Uri
import kotlin.reflect.KProperty

open class SafeMap() : HashMap<String, Any?>() {
    constructor(obj: Any?) : this() {
        if (obj != null) {
            obj::class.java.declaredFields.forEach { m ->
                m.isAccessible = true
                put(m.name, m.get(obj))
            }
        }
    }

    override operator fun get(
        key: String
    ): Any? = runCatching { super.get(key) }.getOrNull()

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T> getValue(
        thisRef: Any?,
        property: KProperty<*>
    ): T {
        val value = get(property.name)
        return when {
            T::class == String::class -> {
                (value?.toString() as? T)
                    ?: if (null is T) null as T else "" as T
            }

            T::class == Int::class -> {
                (value?.toString()?.toIntOrNull() as? T)
                    ?: if (null is T) null as T else 0 as T
            }

            T::class == Long::class -> {
                (value?.toString()?.toLongOrNull() as? T)
                    ?: if (null is T) null as T else 0L as T
            }

            T::class == Boolean::class -> {
                (value?.toString()?.toBoolean() as? T)
                    ?: if (null is T) null as T else false as T
            }

            T::class == Double::class -> {
                (value?.toString()?.toDoubleOrNull() as? T)
                    ?: if (null is T) null as T else 0.0 as T
            }

            T::class == Float::class -> {
                (value?.toString()?.toFloatOrNull() as? T)
                    ?: if (null is T) null as T else 0f as T
            }

            T::class == Uri::class -> {
                val uri =
                    value?.toString()?.let { Uri.parse(it) } ?: if (null is T) null else Uri.EMPTY
                uri as T
            }

            T::class.java.isEnum -> {
                val strValue = value?.toString()
                val enumConstants = T::class.java.enumConstants
                val enumValue = enumConstants?.firstOrNull { it.toString() == strValue } as? T
                if (enumValue != null) enumValue
                else if (null is T) null as T
                else enumConstants?.firstOrNull() as T
            }

            else -> {
                value as? T
                    ?: if (null is T) null as T else throw IllegalStateException("Property ${property.name} is not of type ${T::class}")
            }
        }
    }
}