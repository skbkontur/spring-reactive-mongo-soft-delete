package ru.kontur.spring.soft.delete.callbacks

import com.mongodb.reactivestreams.client.FindPublisher
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

/**
 * @author Konstantin Volivach
 */
class FindCallback(private val query: Document) : ReactiveQueryCollectionCallback<Document> {
    override fun doInCollection(collection: MongoCollection<Document>): FindPublisher<Document> {
        val findPublisher = collection.find(query, Document::class.java)
        return findPublisher
    }
}