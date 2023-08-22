package ru.alcoserver.verushkinrg.dbManager.presentation.model

import ru.alcoserver.verushkinrg.common.data.model.User

data class DBManagerState(
    val working: Boolean = false,
    val availableUsers1: List<User> = emptyList(),
    val availableUsers2: List<User> = emptyList(),
    val usersExpanded1: Boolean = false,
    val usersExpanded2: Boolean = false,
    val user1: User? = null,
    val user2: User? = null,
    val userFilter1: String = "",
    val userFilter2: String = "",
    val errorMessage: String? = null
)