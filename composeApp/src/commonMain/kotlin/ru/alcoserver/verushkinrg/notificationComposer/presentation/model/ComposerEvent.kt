package ru.alcoserver.verushkinrg.notificationComposer.presentation.model

sealed interface ComposerEvent {
    data object OnScreenEnter : ComposerEvent
    data object OnScreenExit : ComposerEvent
    data class OnUserFilterInput(val filter: String) : ComposerEvent
    data object ClearUserFilter : ComposerEvent
    data object OnUsersExpand : ComposerEvent
    data class OnUserTap(val user: User) : ComposerEvent
    data class OnTitleInput(val title: String) : ComposerEvent
    data object OnTitleClear : ComposerEvent
    data object OnSend : ComposerEvent
    data object OnCloseError : ComposerEvent
}