package ru.kontur.spring.soft.delete.reactive.core

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import ru.kontur.spring.soft.delete.reactive.ReactiveMongoSoftDeleteTemplate

@TestConfiguration
class TestConfiguration {

    @Bean
    fun reactiveMongoTemplate(
        mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
        mongoConverter: MongoConverter
    ): ReactiveMongoTemplate {
        return ReactiveMongoSoftDeleteTemplate(
            mongoDatabaseFactory,
            mongoConverter
        )
    }
}