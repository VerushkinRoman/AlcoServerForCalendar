package ru.alcoserver.verushkinrg.notificationService.presentation.model

data class NotificationServiceState(
    val running: Boolean = false,
    val manageDialogOpened: Boolean = false,
    val composeNotificationDialogOpened: Boolean = false,
    val errorMessage: String? = null,
    val serviceAccountPath: String = "",
    val filePickerOpened: Boolean = false,
    val isVisible: Boolean = true
)
