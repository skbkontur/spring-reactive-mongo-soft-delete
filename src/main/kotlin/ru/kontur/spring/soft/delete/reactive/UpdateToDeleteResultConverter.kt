package ru.kontur.spring.soft.delete.reactive

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult

/**
 * @author kostya05983
 */
class UpdateToDeleteResultConverter {

    /**
     * Convert oneResult to other for softDelete
     */
    fun convert(updateResult: UpdateResult): DeleteResult {
        return if (updateResult.wasAcknowledged()) {
            DeleteResult.acknowledged(updateResult.modifiedCount)
        } else {
            DeleteResult.unacknowledged()
        }
    }
}