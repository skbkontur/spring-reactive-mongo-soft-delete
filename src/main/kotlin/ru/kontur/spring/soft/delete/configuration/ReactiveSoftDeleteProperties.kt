package ru.kontur.spring.soft.delete.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * @author Konstantin Volivach
 */
@ConfigurationProperties("ru.kontur.soft-delete")
data class ReactiveSoftDeleteProperties(
    var enabled: Boolean = false,
    var deleteField: String = "deleted",
    var dateField: String = "deletedAt"
)