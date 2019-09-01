package ru.kontur.spring.soft.delete.reactive

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.kontur.spring.soft.delete.reactive.core.MongoBase
import ru.kontur.spring.soft.delete.reactive.core.TestConfiguration

@SpringBootTest(classes = [ReactiveMongoSoftDeleteTemplate::class])
@ExtendWith(SpringExtension::class)
@Import(TestConfiguration::class)
internal class ReactiveMongoSoftDeleteTemplateTest : MongoBase() {


    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoSoftDeleteTemplate

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun test() {

    }
}