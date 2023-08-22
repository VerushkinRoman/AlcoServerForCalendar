package ru.alcoserver.verushkinrg.common.data

interface Repository {
    suspend fun getData(
        document: String,
        collection: String
    ): Map<String, Any>?

    fun <T> saveItem(collection: String, document: String, data: T)
    fun <T> removeItem(collection: String, document: String, data: T)
}