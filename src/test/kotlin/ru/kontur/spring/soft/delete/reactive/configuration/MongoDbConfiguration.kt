package ru.kontur.spring.soft.delete.reactive.configuration

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.env.PropertySource
import java.util.*

class MongoDbConfiguration : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication?) {
        if (environment.getProperty(MONGO_ENABLED_PROPERTY)?.toBoolean() == true) {
            val mongoLogin = environment.getProperty(MONGO_LOGIN_PROPERTY)?.takeIf { it.isNotBlank() }
            val mongoPassword = environment.getProperty(MONGO_PASSWORD_PROPERTY)?.takeIf { it.isNotBlank() }
            val mongoDatabase = environment.getProperty(MONGO_DATABASE_PROPERTY)?.takeIf { it.isNotBlank() }
            val mongoReplicaSet = environment.getProperty(MONGO_REPLICA_SET_PROPERTY)?.takeIf { it.isNotBlank() }

            requireNotNull(mongoLogin) { "Missing property '$MONGO_LOGIN_PROPERTY'" }
            requireNotNull(mongoPassword) { "Missing property '$MONGO_PASSWORD_PROPERTY'" }
            requireNotNull(mongoDatabase) { "Missing property '$MONGO_DATABASE_PROPERTY'" }
            requireNotNull(mongoReplicaSet) { "Missing property '$MONGO_REPLICA_SET_PROPERTY'" }

            val defaultProperties = Properties()
            LoggerFactory.getLogger(javaClass).error("mongodb://$mongoLogin:$mongoPassword@$mongoReplicaSet")

            defaultProperties.setProperty("spring.data.mongodb.database", mongoDatabase)
            defaultProperties.setProperty("spring.data.mongodb.uri", "mongodb://$mongoLogin:$mongoPassword@$mongoReplicaSet")

            val propertySource: PropertySource<Map<String, Any>> =
                PropertiesPropertySource("kontur.realty.web.mongodb", defaultProperties)

            environment.propertySources.addLast(propertySource)
        }
    }

    companion object {
        const val MONGO_ENABLED_PROPERTY = "kontur.realty.web.mongodb.enabled"
        const val MONGO_LOGIN_PROPERTY = "kontur.realty.web.mongodb.login"
        const val MONGO_PASSWORD_PROPERTY = "kontur.realty.web.mongodb.password"
        const val MONGO_REPLICA_SET_PROPERTY = "kontur.realty.web.mongodb.replica-set"
        const val MONGO_DATABASE_PROPERTY = "kontur.realty.web.mongodb.database"
    }
}