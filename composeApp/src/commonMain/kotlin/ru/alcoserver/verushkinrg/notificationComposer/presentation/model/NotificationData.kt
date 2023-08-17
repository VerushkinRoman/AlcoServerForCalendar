package ru.alcoserver.verushkinrg.notificationComposer.presentation.model

import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.ServerTimestamp

data class NotificationData(
    val to: String = "",
    val title: String = "",
    val message: String = "",
    val drinkType: String? = null,
    @ServerTimestamp val timestamp: Timestamp = Timestamp.now()
)
