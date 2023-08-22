package ru.alcoserver.verushkinrg.common.data

import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import ru.alcoserver.verushkinrg.common.data.model.FirebaseUser
import ru.alcoserver.verushkinrg.common.data.model.User
import ru.alcoserver.verushkinrg.common.data.model.toUser
import ru.alcoserver.verushkinrg.common.utils.toDataClass

object UsersRepo {
    suspend fun getUsers(): List<User> = coroutineScope {
        val newUsers = mutableListOf<User>()
        FirestoreClient.getFirestore()
            .collection("Collection_of_all_users")
            .document("Users").get().get().data?.values?.forEach { userData ->
                ensureActive()
                @Suppress("UNCHECKED_CAST")
                (userData as? Map<String, Any>)
                    ?.toDataClass<FirebaseUser>()
                    ?.toUser()
                    ?.also { newUsers.add(it) }
            }
        return@coroutineScope newUsers
    }
}