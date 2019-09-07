package ru.kontur.spring.soft.delete.callbacks

import com.mongodb.reactivestreams.client.FindPublisher
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.springframework.data.mongodb.core.ReactiveCollectionCallback

/**
 * @author Konstantin Volivach
 */
interface ReactiveQueryCollectionCallback<T> : ReactiveCollectionCallback<T> {
    override fun doInCollection(collection: MongoCollection<Document>): FindPublisher<T>
}