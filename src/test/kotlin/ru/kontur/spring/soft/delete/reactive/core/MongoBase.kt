package ru.kontur.spring.soft.delete.reactive.core

abstract class MongoBase {

    companion object {
        const val MONGODB_EXPOSED_PORT = 27017
        const val MONGODB_USERNAME = "test"
        const val MONGODB_PASSWORD = "test"

        @JvmStatic
        val mongoDb: KGenericContainer = KGenericContainer("mongo:4.0.11-xenial")
                .withExposedPorts(MONGODB_EXPOSED_PORT)
                .withEnv("MONGO_INITDB_ROOT_USERNAME", MONGODB_USERNAME)
                .withEnv("MONGO_INITDB_ROOT_PASSWORD", MONGODB_PASSWORD)

        init {
            mongoDb.start()
        }
    }
}