package ru.kontur.spring.soft.delete.configuration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    properties = [
        "ru.kontur.soft-delete.enabled=false"
    ]
)
@Import(TestConfiguration::class)
@EnableAutoConfiguration
class ConfigurationWithoutMongoSoftDel {

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun test() {
        assertThat(reactiveMongoTemplate::class.java.simpleName).isEqualTo(ReactiveMongoTemplate::class.java.simpleName)
    }
}