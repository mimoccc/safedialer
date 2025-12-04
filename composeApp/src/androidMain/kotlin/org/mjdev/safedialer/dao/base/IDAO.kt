package org.mjdev.safedialer.dao.base

//import kotbase.Database
//import kotbase.DatabaseConfiguration
import kotlin.reflect.KProperty

@Suppress("SpellCheckingInspection")
open class IDAO(
    val dbName: String,
//    val config: DatabaseConfiguration? = null
) {
//    val database: Database by lazy {
//        val dbNameValid = dbName.replace(".", "_")
//        if (config != null) {
//            Database(dbNameValid, config)
//        } else {
//            Database(dbNameValid)
//        }
//    }

    inline operator fun <reified R : Any> getValue(
        dao: IDAO,
        property: KProperty<*>
    ): DAOCollection<R> =  dao.collection<R>()

    @Throws(DAOException::class)
    inline fun <reified T : Any> collection(
        collName: String? = T::class.simpleName
    ): DAOCollection<T> = runCatching<DAOCollection<T>> {
//        val cName = collName ?: throw (DAOException("Invalid Collection Name"))
        DAOCollection(
//            database.getCollection(cName) ?: database.createCollection(cName)
        )
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: throw (DAOException("Invalid Collection Name"))
}