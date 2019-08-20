package ru.it.zoo.reactive

import com.mongodb.client.result.DeleteResult
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Mono

/**
 * @author kostya05983
 */
class ReactiveMongoSoftDeleteTemplate(
    mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
    mongoConverter: MongoConverter
) : ReactiveMongoTemplate(mongoDatabaseFactory, mongoConverter) {

    private companion object {
    }

    override fun <T : Any?> doRemove(collectionName: String, query: Query, entityClass: Class<T>?): Mono<DeleteResult> {
        //Reuse removeCollectionCallback
        return super.doRemove(collectionName, query, entityClass)
    }
}