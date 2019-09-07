package ru.kontur.spring.soft.delete.reactive

import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.kontur.spring.soft.delete.reactive.core.SpringContainerBaseTest

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("integration")
internal class ReactiveMongoSoftDeleteTemplateTest : SpringContainerBaseTest() {

    private companion object {
        const val COLLECTION_NAME = "test"
    }

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @BeforeEach
    fun setUp() {
        reactiveMongoTemplate.dropCollection(COLLECTION_NAME).block()
        reactiveMongoTemplate.createCollection(COLLECTION_NAME).block()
    }

    data class TestObject(
        @field:Id
        val id: String,
        val param: String,
        val deleted: Boolean
    )

    @Test
    fun testFindOne() {
        val obj = saveDeletedObject()

        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findOne(query, TestObject::class.java).block()
        assertThat(found).isNull()
    }

    @Test
    fun testExists() {
        val obj = saveDeletedObject()

        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val exists = reactiveMongoTemplate.exists(query, TestObject::class.java).block()
        assertThat(exists).isFalse()
    }

    @Test
    fun testFind() {
        val obj = saveDeletedObject()

        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.find(query, TestObject::class.java).blockFirst()
        assertThat(found).isNull()
    }

    @Test
    fun testFindById() {
        val obj = saveDeletedObject()
        val isPresent = reactiveMongoTemplate.findById(obj.id, TestObject::class.java).blockOptional().isPresent
        assertThat(isPresent).isFalse()
    }

    @Test
    fun testFindDistinct() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findDistinct(
            query, "param", TestObject::class.java,
            String::class.java
        ).blockFirst()
        assertThat(found).isNull()
    }

    @Test
    fun testFindAndModify() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findAndModify(query, Update(), TestObject::class.java).block()
        assertThat(found).isNull()
    }

    @Test
    fun testFindAndReplace() {
        data class Update(
            val param: String
        )

        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findAndReplace(
            query, Update("te")
        ).block()
        assertThat(found).isNull()
    }

    @Test
    fun testFindAndRemoveNotFound() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findAndRemove(query, TestObject::class.java).block()
        assertThat(found).isNull()
    }

    @Test
    fun testFindAndRemove() {
        val obj = saveObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.findAndRemove(query, TestObject::class.java).block()
        assertThat(found).isNotNull

        val deleted = reactiveMongoTemplate.findAndRemove(query, TestObject::class.java).block()
        assertThat(deleted).isNull()
    }

    @Test
    fun testCount() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.count(query, TestObject::class.java).block()
        assertThat(found).isEqualTo(0)
    }

    @Test
    fun testUpsert() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.upsert(query, Update(), TestObject::class.java).block()
        assertThat(found.matchedCount).isEqualTo(0)
    }

    @Test
    fun testUpdateFirst() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.updateFirst(query, Update(), TestObject::class.java).block()
        assertThat(found.matchedCount).isEqualTo(0)
    }

    @Test
    fun testUpdateMulti() {
        val obj = saveDeletedObject()
        val query = Query().addCriteria(Criteria.where("_id").`is`(obj.id))
        val found = reactiveMongoTemplate.updateMulti(query, Update(), TestObject::class.java).block()
        assertThat(found.matchedCount).isEqualTo(0)
    }

    @Test
    fun testRemove() {

    }

    @Test
    fun testFindAll() {
        val obj = saveDeletedObject()
        val found = reactiveMongoTemplate.findAll(TestObject::class.java).blockFirst()
        assertThat(found).isNull()
    }

    fun saveObject(): TestObject {
        val testObject = TestObject(
            id = ObjectId().toHexString(),
            param = "Test",
            deleted = false
        )
        return reactiveMongoTemplate.save(testObject).block()
    }

    fun saveDeletedObject(): TestObject {
        val testObject = TestObject(
            id = ObjectId().toHexString(),
            param = "Test",
            deleted = true
        )
        return reactiveMongoTemplate.save(testObject).block()
    }
}