package org.mjdev.safedialer.extensions

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

inline fun <reified T> className(): String = T::class.java.simpleName

fun className(
    clazz: Class<*>
): String = clazz.simpleName

fun className(
    kclass: KClass<*>
): String = kclass.java.simpleName

fun Any.className(): String = genericClassName()

fun Any.genericClassName(
    index: Int = 0
): String {
    val type = (javaClass.genericSuperclass as? ParameterizedType)
        ?.actualTypeArguments
        ?.getOrNull(index)
    return if (type is Class<*>) type.simpleName
    else (this as? KClass<*>)?.java?.simpleName ?: javaClass.simpleName
}
