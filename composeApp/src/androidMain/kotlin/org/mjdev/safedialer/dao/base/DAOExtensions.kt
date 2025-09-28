package org.mjdev.safedialer.dao.base

@Suppress("unused")
object DAOExtensions {

//    fun Collection.load(
//        docId: String
//    ): Document? = getDocument(docId)

//    fun Collection.update(
//        docId: String,
//        update: (Document) -> Unit
//    ) {
//        getDocument(docId)?.let { doc ->
//            doc.toMutable().apply {
//                update(this)
//                save(this)
//            }
//        }
//    }

//    fun Collection.queryDocs(
//        expression: Expression = Expression
//            .property("language")
//            .equalTo(
//                Expression.string("Kotlin")
//            )
//    ): ResultSet = QueryBuilder.select(SelectResult.all())
//        .from(DataSource.collection(this))
//        .where(expression)
//        .execute()

//    fun replicate(
//        uri: String,
//        userName: String,
//        password: String,
//        collection: Collection,
//    ): Flow<ReplicatorChange>? {
//        val collConfig = CollectionConfiguration()
//            .setPullFilter { doc, _ -> "Java" == doc.getString("language") }
//        val repl = Replicator(
//            ReplicatorConfigurationFactory.newConfig(
//                target = URLEndpoint(uri),
//                collections = mapOf(setOf(collection) to collConfig),
//                type = ReplicatorType.PUSH_AND_PULL,
//                authenticator = BasicAuthenticator(
//                    username = userName,
//                    password.toCharArray()
//                )
//            )
//        )
//        // Listen to replicator change events.
//        val changes = repl.replicatorChangesFlow()
//        // Start replication.
//        repl.start()
//        return changes
//    }

}
