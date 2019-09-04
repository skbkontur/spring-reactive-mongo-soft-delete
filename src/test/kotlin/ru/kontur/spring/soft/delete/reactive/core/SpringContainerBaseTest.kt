package ru.kontur.spring.soft.delete.reactive.core

import ru.kontur.spring.soft.delete.reactive.configuration.MongoDbConfiguration

abstract class SpringContainerBaseTest : MongoBase() {
    init {
        System.setProperty(MongoDbConfiguration.MONGO_ENABLED_PROPERTY, "true")
        System.setProperty(MongoDbConfiguration.MONGO_LOGIN_PROPERTY, "test")
        System.setProperty(MongoDbConfiguration.MONGO_PASSWORD_PROPERTY, "test")
        System.setProperty(MongoDbConfiguration.MONGO_DATABASE_PROPERTY, "test")
        System.setProperty(
            MongoDbConfiguration.MONGO_REPLICA_SET_PROPERTY,
            "${mongoDb.containerIpAddress}:${mongoDb.getMappedPort(MONGODB_EXPOSED_PORT)}"
        )
        System.setProperty("server.port", "8080")
    }
}