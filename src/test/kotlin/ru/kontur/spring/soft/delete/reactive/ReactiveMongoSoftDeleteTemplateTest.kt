package ru.kontur.spring.soft.delete.reactive

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.kontur.spring.soft.delete.reactive.core.MongoBase
import ru.kontur.spring.soft.delete.reactive.core.MongoClientTest
import ru.kontur.spring.soft.delete.reactive.core.SpringContainerBaseTest
import ru.kontur.spring.soft.delete.reactive.core.TestConfiguration
import java.util.UUID

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
    fun test() {
        val id = UUID.randomUUID()
        val testObject = TestObject(
            id = id.toString(),
            param = "Test",
            deleted = true
        )
        reactiveMongoTemplate.save(testObject).block()

        val query = Query().addCriteria(Criteria.where("_id").`is`(id.toString()))
        val found = reactiveMongoTemplate.find(query, TestObject::class.java).blockFirst()
        assertThat(found).isNull()
    }
}