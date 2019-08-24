package ru.it.zoo.reactive

import com.mongodb.WriteConcern
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.convert.QueryMapper
import org.springframework.data.mongodb.core.convert.UpdateMapper
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.data.mongodb.core.query.Collation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.util.Assert
import reactor.core.publisher.Mono

/**
 * @author kostya05983
 */
class ReactiveMongoSoftDeleteTemplate(
    mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
    mongoConverter: MongoConverter
) : ReactiveMongoTemplate(mongoDatabaseFactory, mongoConverter) {
    private val mappingContext: MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty> =
        mongoConverter.mappingContext
    private val queryMapper: QueryMapper = QueryMapper(mongoConverter)
    private val updateMapper: UpdateMapper = UpdateMapper(mongoConverter)
    private var writeConcernOverride: WriteConcern? = null

    private companion object {
        private val REMOVE_CRITERIA = Criteria().orOperator(
            Criteria.where("deleted").exists(false),
            Criteria.where("deleted").`is`(false)
        )

        private val UPDATE_DEL = Update().apply {
            set("deleted", true)
        }
    }

    override fun setWriteConcern(writeConcern: WriteConcern?) {
        writeConcernOverride = writeConcern
        super.setWriteConcern(writeConcern)
    }

    override fun <T : Any?> doRemove(
        collectionName: String,
        query: Query?,
        entityClass: Class<T>?
    ): Mono<DeleteResult> {
        if (query == null) {
            throw InvalidDataAccessApiUsageException("Query passed in to remove can't be null!")
        }

        Assert.hasText(collectionName, "Collection name must not be null or empty!")

        val queryObject = query.queryObject
        val entity = getPersistentEntity(entityClass)

        val updateToDeleteResultConverter = UpdateToDeleteResultConverter()
        return execute(collectionName) { collection ->
            val updateQuery = queryMapper.getMappedObject(queryObject, entity)
            updateQuery.merge(REMOVE_CRITERIA.criteriaObject)

            val updateObj = updateMapper.getMappedObject(UPDATE_DEL.updateObject, entity)
            val mongoAction = MongoAction(
                writeConcernOverride, MongoActionOperation.UPDATE, collectionName, entityClass,
                updateObj, updateQuery
            )

            val writeConcernToUse = prepareWriteConcern(mongoAction)
            val collectionToUse = prepareCollection(collection, writeConcernToUse)

            val updateOptions = UpdateOptions()
            query.collation.map { it.toMongoCollation() }.ifPresent {
                updateOptions.collation(it)
            }

            collectionToUse.updateMany(queryObject, updateObj, updateOptions)
        }.map {
            updateToDeleteResultConverter.convert(it)
        }.next()
    }

    override fun doUpdate(
        collectionName: String,
        query: Query,
        update: Update?,
        entityClass: Class<*>?,
        upsert: Boolean,
        multi: Boolean
    ): Mono<UpdateResult> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.doUpdate(collectionName, query, update, entityClass, upsert, multi)
    }

    override fun <T : Any?> doFindAndModify(
        collectionName: String,
        query: Document,
        fields: Document,
        sort: Document,
        entityClass: Class<T>,
        update: Update,
        options: FindAndModifyOptions
    ): Mono<T> {
        query.merge(REMOVE_CRITERIA.criteriaObject)
        return super.doFindAndModify(collectionName, query, fields, sort, entityClass, update, options)
    }

    override fun <T : Any?> doFindOne(
        collectionName: String,
        query: Document,
        fields: Document?,
        entityClass: Class<T>,
        collation: Collation?
    ): Mono<T> {
        query.merge(REMOVE_CRITERIA.criteriaObject)
        return super.doFindOne(collectionName, query, fields, entityClass, collation)
    }

    private fun prepareCollection(
        collection: MongoCollection<Document>,
        writeConcernToUse: WriteConcern?
    ): MongoCollection<Document> {
        var collectionToUse = collection

        if (writeConcernToUse != null) {
            collectionToUse = collectionToUse.withWriteConcern(writeConcernToUse)
        }
        return collectionToUse
    }

    private fun getPersistentEntity(type: Class<*>?): MongoPersistentEntity<*>? {
        return if (type == null) null else mappingContext.getPersistentEntity(type)
    }

    private fun Document.merge(`in`: Document) {
        for ((key, value) in `in`) {
            put(key, value)
        }
    }
}