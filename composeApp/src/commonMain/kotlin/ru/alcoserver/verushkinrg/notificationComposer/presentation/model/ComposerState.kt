package ru.alcoserver.verushkinrg.notificationComposer.presentation.model

data class ComposerState(
    val availableUsers: List<User> = emptyList(),
    val usersExpanded: Boolean = false,
    val user: User? = null,
    val userFilter: String = "",
    val title: String = "",
    val errorMessage: String? = null
)
