package ru.kontur.spring.soft.delete.callbacks

import org.bson.Document

/**
 * @author Konstantin Volivach
 */
interface DocumentCallback<T> {

    fun doWith(obj: Document?): T?
}