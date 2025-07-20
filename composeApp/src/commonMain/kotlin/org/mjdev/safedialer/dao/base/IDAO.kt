package org.mjdev.safedialer.dao.base

import kotbase.Database
import kotlin.reflect.KProperty

@Suppress("SpellCheckingInspection")
open class IDAO(
    val dbName: String,
) {
    val database: Database by lazy { Database(dbName) }

    inline operator fun <reified R : Any> getValue(
        dao: IDAO,
        property: KProperty<*>
    ): DAOCollection<R> =
        dao.collection<R>()

    @Throws(DAOException::class)
    inline fun <reified T : Any> collection(
        collName: String? = T::class.simpleName
    ): DAOCollection<T> = runCatching<DAOCollection<T>> {
        val cName = collName ?: throw (DAOException("Invalid Collection Name"))
        DAOCollection(database.getCollection(cName) ?: database.createCollection(cName))
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: throw (DAOException("Invalid Collection Name"))
}