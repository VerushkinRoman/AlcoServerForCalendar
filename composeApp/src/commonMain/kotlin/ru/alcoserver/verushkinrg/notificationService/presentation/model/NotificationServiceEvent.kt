package ru.alcoserver.verushkinrg.notificationService.presentation.model

sealed interface NotificationServiceEvent {
    data object Start : NotificationServiceEvent
    data object Stop : NotificationServiceEvent
    data object OpenManageDialog : NotificationServiceEvent
    data object CloseManageDialog : NotificationServiceEvent
    data object OpenComposerDialog : NotificationServiceEvent
    data object CloseComposerDialog : NotificationServiceEvent
    data object CloseErrorMessage : NotificationServiceEvent
    data object OpenFilePicker : NotificationServiceEvent
    data class CloseFilePicker(val path: String) : NotificationServiceEvent
}