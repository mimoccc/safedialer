package org.mjdev.safedialer.helpers

import kotlin.reflect.KClass

class Cache() {
    val cacheData = mutableMapOf<String, Any>()

    inline fun <reified T> contains(): Boolean =
        cacheData.containsKey(T::class.simpleName)

    operator fun get(key: KClass<*>): Any? =
        cacheData[key.simpleName!!]

    operator fun get(key: String): Any? =
        cacheData[key]

    operator fun set(key: String, value: Any) {
        cacheData[key] = value
    }

    operator fun set(key: KClass<*>, value: Any) {
        cacheData[key.simpleName!!] = value
    }

    fun remove(key: KClass<*>) =
        cacheData.remove(key.simpleName!!)

    fun clear() =
        cacheData.clear()
}