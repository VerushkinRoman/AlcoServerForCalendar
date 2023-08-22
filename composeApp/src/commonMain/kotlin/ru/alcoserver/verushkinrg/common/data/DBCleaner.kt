package ru.alcoserver.verushkinrg.common.data

import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.data.model.CollectionData
import ru.alcoserver.verushkinrg.common.data.model.Day
import ru.alcoserver.verushkinrg.common.settings.SettingsRepository
import ru.alcoserver.verushkinrg.common.utils.toDataClass
import java.time.LocalDate

object DBCleaner {
    private val settingsRepository: SettingsRepository = Inject.instance()

    suspend fun cleanDatabase(): Int = coroutineScope {
        val collectionsData =
            FirestoreClient.getFirestore().listCollections().mapNotNull { collectionReference ->
                ensureActive()
                val days = collectionReference.document("Dates").get().get().data?.values
                    ?.map { dayData ->
                        @Suppress("UNCHECKED_CAST")
                        (dayData as Map<String, Any>).toDataClass<Day>()
                    } ?: emptyList()

                val collectionId = collectionReference.id
                if (collectionId == "Collection_of_all_users") return@mapNotNull null
                CollectionData(
                    id = collectionId,
                    collectionReference = collectionReference,
                    days = days
                )
            }

        val halfYearOld = LocalDate.now().minusMonths(6).toEpochDay()

        val outdatedCollections = collectionsData.filter { collectionData ->
            collectionData.days.isEmpty() || collectionData.days.maxOf { it.date } < halfYearOld
        }

        deleteCollections(outdatedCollections.map { it.collectionReference })

        val outdatedCollectionIds = outdatedCollections.map { it.id }
        deleteUsersFromUsersDocument(outdatedCollectionIds)
        deleteUsersAuth(outdatedCollectionIds, collectionsData.map { it.id })

        settingsRepository.dbCleanupDate = LocalDate.now().toEpochDay()

        return@coroutineScope outdatedCollections.size
    }

    private suspend fun deleteCollections(collections: List<CollectionReference>) = coroutineScope {
        collections.forEach { collectionData ->
            ensureActive()
            val future = collectionData.get()
            val documents = future.get().documents
            for (document in documents) {
                document.reference.delete()
            }
        }
    }

    private fun deleteUsersFromUsersDocument(usersToDelete: List<String?>) {
        val deletionMap = mutableMapOf<String, Any>()

        usersToDelete.mapNotNull { it }.forEach {
            deletionMap[it] = FieldValue.delete()
        }
        FirestoreClient.getFirestore()
            .collection("Collection_of_all_users")
            .document("Users")
            .set(deletionMap, SetOptions.merge())
    }

    private suspend fun deleteUsersAuth(
        outdatedCollectionIds: List<String>,
        collectionIds: List<String>
    ) = coroutineScope {
        val firebaseAuth = FirebaseAuth.getInstance()
        val page = firebaseAuth.listUsers(null)
        for (user in page.iterateAll()) {
            ensureActive()
            val userUid = user.uid
            if ((user.email ?: userUid) in outdatedCollectionIds) {
                firebaseAuth.deleteUser(userUid)
            }
            if ((user.email ?: userUid) !in collectionIds) {
                firebaseAuth.deleteUser(userUid)
            }
        }
    }
}