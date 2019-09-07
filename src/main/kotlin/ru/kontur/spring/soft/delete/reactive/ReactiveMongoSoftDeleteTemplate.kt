package ru.kontur.spring.soft.delete.reactive

import com.mongodb.WriteConcern
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.data.convert.EntityReader
import org.springframework.data.mapping.context.MappingContext
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.*
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.convert.QueryMapper
import org.springframework.data.mongodb.core.convert.UpdateMapper
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent
import org.springframework.data.mongodb.core.query.Collation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.util.Assert
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.kontur.spring.soft.delete.callbacks.FindCallback
import ru.kontur.spring.soft.delete.callbacks.ReactiveQueryCollectionCallback

/**
 * @author kostya05983
 */
class ReactiveMongoSoftDeleteTemplate(
    mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
    private val mongoConverter: MongoConverter?
) : ReactiveMongoTemplate(mongoDatabaseFactory, mongoConverter) {
    private val mappingContext: MappingContext<out MongoPersistentEntity<*>, MongoPersistentProperty>? =
        mongoConverter?.mappingContext
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

        query.addCriteria(REMOVE_CRITERIA)
        val entity = getPersistentEntity(entityClass)

        val updateToDeleteResultConverter = UpdateToDeleteResultConverter()
        return execute(collectionName) { collection ->
            val queryObj = queryMapper.getMappedObject(query.queryObject, entity)

            val updateObj = updateMapper.getMappedObject(UPDATE_DEL.updateObject, entity)

            val mongoAction = MongoAction(
                writeConcernOverride, MongoActionOperation.UPDATE, collectionName, entityClass,
                updateObj, queryObj
            )

            val writeConcernToUse = prepareWriteConcern(mongoAction)
            val collectionToUse = prepareCollection(collection, writeConcernToUse)

            val updateOptions = UpdateOptions()
            query.collation.map { it.toMongoCollation() }.ifPresent {
                updateOptions.collation(it)
            }

            if (queryObj.containsKey("_id")) {
                collectionToUse.updateOne(queryObj, updateObj, updateOptions)
            } else {
                collectionToUse.updateMany(queryObj, updateObj, updateOptions)
            }
        }.map {
            updateToDeleteResultConverter.convert(it)
        }.next()
    }

    override fun <T : Any?> doFindAndRemove(
        collectionName: String,
        query: Document,
        fields: Document,
        sort: Document,
        collation: Collation?,
        entityClass: Class<T>
    ): Mono<T> {
        query.merge(REMOVE_CRITERIA.criteriaObject)
        return super.doFindAndRemove(collectionName, query, fields, sort, collation, entityClass)
    }

    override fun doUpdate(
        collectionName: String,
        query: Query,
        update: Update?,
        entityClass: Class<*>?,
        upsert: Boolean,
        multi: Boolean
    ): Mono<UpdateResult> {
        if (!upsert) {
            query.addCriteria(REMOVE_CRITERIA)
        }
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

    override fun <T : Any?> find(query: Query, entityClass: Class<T>): Flux<T> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.find(query, entityClass)
    }

    override fun <T : Any?> findAll(entityClass: Class<T>, collectionName: String): Flux<T?> { // TODO overriding
        val document = Document()
        document.merge(REMOVE_CRITERIA.criteriaObject)
        return executeFindMultiInternal(
            FindCallback(document), ReadDocumentCallback(
                mongoConverter, entityClass, collectionName
            ), collectionName
        )
    }

    override fun <T : Any?> findDistinct(
        query: Query,
        field: String,
        collectionName: String,
        entityClass: Class<*>,
        resultClass: Class<T>
    ): Flux<T> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.findDistinct(query, field, collectionName, entityClass, resultClass)
    }

    override fun <S : Any?, T : Any?> findAndReplace(
        query: Query,
        replacement: S,
        options: FindAndReplaceOptions,
        entityType: Class<S>,
        collectionName: String,
        resultType: Class<T>
    ): Mono<T> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.findAndReplace(query, replacement, options, entityType, collectionName, resultType)
    }

    override fun exists(query: Query, entityClass: Class<*>?, collectionName: String): Mono<Boolean> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.exists(query, entityClass, collectionName)
    }

    override fun count(query: Query, entityClass: Class<*>?, collectionName: String): Mono<Long> {
        query.addCriteria(REMOVE_CRITERIA)
        return super.count(query, entityClass, collectionName)
    }

    private fun <T> executeFindMultiInternal(
        collectionCallback: ReactiveQueryCollectionCallback<Document>,
        objectCallback: ru.kontur.spring.soft.delete.callbacks.DocumentCallback<T>,
        collectionName: String
    ): Flux<T?> {
        return createFlux(collectionName) { collection ->
            val findPublisher = collectionCallback.doInCollection(collection)
            Flux.from(findPublisher).map { objectCallback.doWith(it) }
        }
    }

    inner class ReadDocumentCallback<T>(
        private val reader: EntityReader<in T, Bson>?,
        private val type: Class<T>,
        private val collectionName: String
    ) : ru.kontur.spring.soft.delete.callbacks.DocumentCallback<T> {

        override fun doWith(obj: Document?): T? {
            if (obj != null) {
                maybeEmitEvent(AfterLoadEvent(obj, type, collectionName))
            }
            val source: T? = reader?.read(type, obj)
            if (source != null) {
                maybeEmitEvent(AfterConvertEvent(obj, source, collectionName))
            }
            return source
        }
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
        return if (type == null) null else mappingContext?.getPersistentEntity(type)
    }

    private fun Document.merge(`in`: Document) {
        for ((key, value) in `in`) {
            put(key, value)
        }
    }
}