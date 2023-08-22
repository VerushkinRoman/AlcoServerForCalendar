package ru.alcoserver.verushkinrg.dbManager.presentation.model

import ru.alcoserver.verushkinrg.common.data.model.User

sealed interface DBManagerEvent {
    data object ClearDB : DBManagerEvent
    data object OnScreenEnter : DBManagerEvent
    data object OnScreenExit : DBManagerEvent
    data class OnUser1FilterInput(val filter: String) : DBManagerEvent
    data class OnUser2FilterInput(val filter: String) : DBManagerEvent
    data object ClearUser1Filter : DBManagerEvent
    data object ClearUser2Filter : DBManagerEvent
    data object OnUsers1Expand : DBManagerEvent
    data object OnUsers2Expand : DBManagerEvent
    data class OnUser1Tap(val user: User) : DBManagerEvent
    data class OnUser2Tap(val user: User) : DBManagerEvent
    data object MakeUsersFriends : DBManagerEvent
    data object UnfriendUsers : DBManagerEvent
    data object OnCloseError : DBManagerEvent
}