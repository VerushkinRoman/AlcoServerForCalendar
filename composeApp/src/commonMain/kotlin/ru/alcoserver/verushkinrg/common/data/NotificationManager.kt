package ru.alcoserver.verushkinrg.common.data

import com.google.firebase.cloud.FirestoreClient
import ru.alcoserver.verushkinrg.common.data.model.NotificationData

object NotificationManager {
    fun sendNotification(
        notificationData: NotificationData
    ) {
        FirestoreClient.getFirestore()
            .collection("Notifications")
            .document()
            .set(notificationData)
    }
}