package ru.kontur.spring.soft.delete.configuration

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoConverter
import ru.kontur.spring.soft.delete.reactive.ReactiveMongoSoftDeleteTemplate

/**
 * @author kostya05983
 */
@ConditionalOnClass(ReactiveMongoTemplate::class)
@ConditionalOnBean(ReactiveMongoTemplate::class)
@ConditionalOnProperty(prefix = "ru.kontur.soft-delete", name = ["enabled"], havingValue = "true")
@EnableConfigurationProperties(ReactiveSoftDeleteProperties::class)
@AutoConfigureAfter(MongoReactiveDataAutoConfiguration::class)
class ReactiveSoftDeleteAutoConfiguration {

    @Bean
    @Primary
    fun reactiveMongoSoftTemplate(
        mongoDatabaseFactory: ReactiveMongoDatabaseFactory,
        mongoConverter: MongoConverter
    ): ReactiveMongoTemplate {
        return ReactiveMongoSoftDeleteTemplate(
            mongoDatabaseFactory,
            mongoConverter
        )
    }
}