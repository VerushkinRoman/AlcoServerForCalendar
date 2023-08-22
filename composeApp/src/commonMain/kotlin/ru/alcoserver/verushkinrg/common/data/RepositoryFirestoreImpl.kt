package ru.alcoserver.verushkinrg.common.data

import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.SetOptions
import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.coroutineScope

class RepositoryFirestoreImpl : Repository {

    override suspend fun getData(
        document: String,
        collection: String
    ): Map<String, Any>? = coroutineScope {
        return@coroutineScope FirestoreClient.getFirestore()
            .collection(collection)
            .document(document)
            .get().get().data
    }

    override fun <T> saveItem(collection: String, document: String, data: T) =
        changeItem(collection, document, data, false)

    override fun <T> removeItem(collection: String, document: String, data: T) =
        changeItem(collection, document, data, true)

    private fun <T> changeItem(collection: String, document: String, data: T, delete: Boolean) {
        val value: Any? = if (delete) FieldValue.delete() else data

        FirestoreClient.getFirestore()
            .collection(collection)
            .document(document)
            .set(mapOf(data.toString() to value), SetOptions.merge())
    }
}