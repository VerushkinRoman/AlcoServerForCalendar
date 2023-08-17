package ru.alcoserver.verushkinrg.notificationService.presentation.messenger

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.NotificationData

object Messenger {
    fun sendMessage(notificationData: NotificationData): String? {
        val message: Message = Message.builder()
            .setToken(notificationData.to)
            .putData("title", notificationData.title)
            .putData("message", notificationData.message)
            .putData("drinkType", notificationData.drinkType.toString())
            .setAndroidConfig(
                AndroidConfig.builder()
                    .setDirectBootOk(true)
                    .build()
            )
            .build()

        return FirebaseMessaging.getInstance().send(message)
    }
}
