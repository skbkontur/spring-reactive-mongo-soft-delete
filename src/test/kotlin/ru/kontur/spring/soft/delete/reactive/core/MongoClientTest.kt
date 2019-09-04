package ru.kontur.spring.soft.delete.reactive.core

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients

abstract class MongoClientTest : MongoBase() {
    protected fun createMongoClient(): MongoClient {
        val connectionString =
            "mongodb://$MONGODB_USERNAME:$MONGODB_PASSWORD@${mongoDb.containerIpAddress}:${mongoDb.getMappedPort(
                MONGODB_EXPOSED_PORT
            )}"

        return MongoClients.create(connectionString)
    }
}