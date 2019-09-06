package ru.kontur.spring.soft.delete.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.nhaarman.mockitokotlin2.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfiguration {
    @Bean
    fun mongoClient(): MongoClient {
        return mock()
    }
}