package ru.it.zoo.reactive

import com.mongodb.client.result.DeleteResult
import com.mongodb.lang.Nullable
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.ReactiveCollectionCallback
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent
import org.springframework.data.mongodb.core.query.Query
import org.springframework.util.Assert
import reactor.core.publisher.Mono

/**
 * @author kostya05983
 */
class ReactiveMongoSoftDeleteTemplate(
    mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
    mongoConverter: MongoConverter
) : ReactiveMongoTemplate(mongoDatabaseFactory, mongoConverter) {

    private val mappingContext: MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>

    init {
        mappingContext = mongoConverter.mappingContext
    }

    private companion object {
    }

    override fun <T : Any?> doRemove(
        collectionName: String,
        query: Query?,
        entityClass: Class<T>?
    ): Mono<DeleteResult> {
        //Reuse removeCollectionCallback
        if (query == null) {
            throw InvalidDataAccessApiUsageException("Query passed in to remove can't be null!")
        }

        Assert.hasText(collectionName, "Collection name must not be null or empty!")

        val queryObject = query.queryObject
        val entity = getPersistentEntity(entityClass)

        execute(collectionName, ReactiveCollectionCallback<T> {
            TODO("Update logic instead of remove")
        }).doOnNext {
            //            maybeEmitEvent(AfterDeleteEvent<>())
        }.next()
        return super.doRemove(collectionName, query, entityClass)
    }

    private fun getPersistentEntity(type: Class<*>?): MongoPersistentEntity<*>? {
        return if (type == null) null else mappingContext.getPersistentEntity(type)
    }
}