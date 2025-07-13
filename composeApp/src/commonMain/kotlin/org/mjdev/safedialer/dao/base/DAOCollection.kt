package org.mjdev.safedialer.dao.base

import kotbase.ConcurrencyControl
import kotbase.DataSource
import kotbase.Document
import kotbase.Expression
import kotbase.Meta
import kotbase.MutableDocument
import kotbase.QueryBuilder
import kotbase.Result
import kotbase.ResultSet
import kotbase.SelectResult
import org.mjdev.safedialer.helpers.JsonHelper.fromJson
import org.mjdev.safedialer.helpers.JsonHelper.toJson
import kotlin.collections.get
import kotbase.Collection as DBCollection

class DAOCollection<T : Any>(
    val collection: DBCollection
) {
    fun add(
        obj: T,
        concurrency: ConcurrencyControl = ConcurrencyControl.LAST_WRITE_WINS,
    ): String? = runCatching {
        val json = obj.toJson()
        val doc = MutableDocument().setJSON(json)
        collection.save(doc, concurrency)
        doc.id
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull()

    inline fun <reified T : Any> asList(
        expression: Expression? = null,
        limit: Int? = null
    ): List<T> = runCatching<List<T>> {
        query(
            select = SelectResult.all(),
            expression = expression,
            limit = limit
        ).allResults()
            .map { r: Result ->
                val map = r.toMap()[T::class.simpleName]
                val json = map.toJson()
                fromJson<T>(json)
            }
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull() ?: emptyList()

    fun query(
        expression: Expression? = null,
        limit: Int? = null,
        select: SelectResult = SelectResult.all(),
    ): ResultSet = QueryBuilder
        .select(select)
        .from(DataSource.collection(collection))
        .let { q -> if (expression == null) q else q.where(expression) }
        .let { q -> if (limit == null) q else q.limit(Expression.intValue(limit)) }
        .execute()

    fun allIds(): List<String> = query(
        select = SelectResult.expression(Meta.id)
    ).allResults().mapNotNull { result ->
        result.getString(0)
    }

    fun allDocuments(): List<Document> = allIds().mapNotNull { docId ->
        collection.getDocument(docId)
    }

    fun clear() = runCatching {
        allDocuments().let { docsToDelete ->
            collection.database.inBatch {
                docsToDelete.forEach { doc ->
                    try {
                        collection.delete(doc)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }
}
