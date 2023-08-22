package ru.alcoserver.verushkinrg.dbManager.presentation

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.data.DBCleaner
import ru.alcoserver.verushkinrg.common.data.NotificationManager
import ru.alcoserver.verushkinrg.common.data.Repository
import ru.alcoserver.verushkinrg.common.data.UsersRepo
import ru.alcoserver.verushkinrg.common.data.model.Contact
import ru.alcoserver.verushkinrg.common.data.model.Friend
import ru.alcoserver.verushkinrg.common.data.model.NotificationData
import ru.alcoserver.verushkinrg.common.data.model.User
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchers
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerEvent
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerState

class DBManagerViewModel : ViewModel() {
    private val _state: MutableStateFlow<DBManagerState> = MutableStateFlow(DBManagerState())
    val state: StateFlow<DBManagerState> = _state.asStateFlow()

    private val coroutinesDispatchers: CoroutinesDispatchers = Inject.instance()
    private val repository: Repository = Inject.instance()
    private var users: List<User> = emptyList()

    fun onEvent(event: DBManagerEvent) {
        when (event) {
            is DBManagerEvent.ClearDB -> clearDB()
            is DBManagerEvent.OnScreenEnter -> updateUsers()
            is DBManagerEvent.OnScreenExit -> onCleared()
            is DBManagerEvent.OnUser1FilterInput -> filterUsers1(event.filter)
            is DBManagerEvent.OnUser2FilterInput -> filterUsers2(event.filter)
            is DBManagerEvent.ClearUser1Filter -> clearUserFilter1()
            is DBManagerEvent.ClearUser2Filter -> clearUserFilter2()
            is DBManagerEvent.OnUser1Tap -> onUser1Selected(event.user)
            is DBManagerEvent.OnUser2Tap -> onUser2Selected(event.user)
            is DBManagerEvent.OnUsers1Expand -> expandUsers1()
            is DBManagerEvent.OnUsers2Expand -> expandUsers2()
            is DBManagerEvent.OnCloseError -> closeError()
            is DBManagerEvent.MakeUsersFriends -> makeUsersFriends()
            is DBManagerEvent.UnfriendUsers -> unfriendUsers()
        }
    }

    private fun unfriendUsers() {
        changeFriendship(makeUsersFriends = false)
    }

    private fun makeUsersFriends() {
        changeFriendship(makeUsersFriends = true)
    }

    private fun changeFriendship(makeUsersFriends: Boolean) {
        val user1 = state.value.user1
        val user2 = state.value.user2

        if (user1 == null || user2 == null) return

        val friend1 = Friend(
            name = user1.nickName,
            email = user1.email
        )

        val friend2 = Friend(
            name = user2.nickName,
            email = user2.email
        )

        val contact1 = Contact(
            names = listOf(user1.nickName),
            email = user1.email
        )

        val contact2 = Contact(
            names = listOf(user2.nickName),
            email = user2.email
        )

        val user1Notification = NotificationData(
            to = user1.id,
            title = user2.nickName,
            message = if (makeUsersFriends) ADDED_YOU.toString() else REMOVED_YOU.toString()
        )

        val user2Notification = NotificationData(
            to = user2.id,
            title = user1.nickName,
            message = if (makeUsersFriends) ADDED_YOU.toString() else REMOVED_YOU.toString()
        )

        if (makeUsersFriends) {
            repository.saveItem(
                collection = user1.email,
                document = FRIENDS_DOCUMENT,
                data = friend2
            )

            repository.saveItem(
                collection = user2.email,
                document = FRIENDS_DOCUMENT,
                data = friend1
            )

            repository.saveItem(
                collection = user1.email,
                document = SHARE_DOCUMENT,
                data = contact2
            )

            repository.saveItem(
                collection = user2.email,
                document = SHARE_DOCUMENT,
                data = contact1
            )
        } else {
            repository.removeItem(
                collection = user1.email,
                document = FRIENDS_DOCUMENT,
                data = friend2
            )

            repository.removeItem(
                collection = user2.email,
                document = FRIENDS_DOCUMENT,
                data = friend1
            )

            repository.removeItem(
                collection = user1.email,
                document = SHARE_DOCUMENT,
                data = contact2
            )

            repository.removeItem(
                collection = user2.email,
                document = SHARE_DOCUMENT,
                data = contact1
            )
        }

        NotificationManager.sendNotification(user1Notification)
        NotificationManager.sendNotification(user2Notification)
    }

    private fun clearDB() {
        viewModelScope.launch(coroutinesDispatchers.io) {
            _state.update { it.copy(working = true) }
            try {
                val cleanedCollections = DBCleaner.cleanDatabase()
                _state.update {
                    it.copy(
                        errorMessage = "Cleanup finished!\nDeleted collections: $cleanedCollections"
                    )
                }
            } catch (e: Exception) {
                showError(e.message ?: e.toString())
            }
            _state.update { it.copy(working = false) }
        }
    }

    private fun onUser1Selected(user: User) {
        _state.update {
            it.copy(
                user1 = user,
                usersExpanded1 = false
            )
        }
    }

    private fun onUser2Selected(user: User) {
        _state.update {
            it.copy(
                user2 = user,
                usersExpanded2 = false
            )
        }
    }

    private fun expandUsers1() {
        val expanded = state.value.usersExpanded1
        _state.update { it.copy(usersExpanded1 = !expanded) }
    }

    private fun expandUsers2() {
        val expanded = state.value.usersExpanded2
        _state.update { it.copy(usersExpanded2 = !expanded) }
    }

    private fun filterUsers1(filter: String) {
        _state.update {
            it.copy(
                userFilter1 = filter,
                availableUsers1 = getFilteredUsers(filter)
            )
        }
    }

    private fun filterUsers2(filter: String) {
        _state.update {
            it.copy(
                userFilter2 = filter,
                availableUsers2 = getFilteredUsers(filter)
            )
        }
    }

    private fun getFilteredUsers(filter: String): List<User> {
        return users.filter {
            filter.lowercase() in it.email.lowercase() || filter in it.nickName.lowercase()
        }
    }

    private fun updateUsers() {
        viewModelScope.launch(coroutinesDispatchers.io) {
            val newUsers = try {
                UsersRepo.getUsers()
            } catch (e: Exception) {
                showError(e.message ?: e.toString())
                emptyList()
            }
            users = newUsers
            _state.update { it.copy(availableUsers1 = newUsers, availableUsers2 = newUsers) }
        }
    }

    private fun clearUserFilter1() {
        _state.update {
            it.copy(
                userFilter1 = "",
                availableUsers1 = users
            )
        }
    }

    private fun clearUserFilter2() {
        _state.update {
            it.copy(
                userFilter2 = "",
                availableUsers2 = users
            )
        }
    }

    private fun showError(message: String) {
        _state.update { it.copy(errorMessage = message) }
    }

    private fun closeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        _state.update { DBManagerState() }
        viewModelScope.coroutineContext.cancelChildren()
    }

    companion object {
        private const val FRIENDS_DOCUMENT = "Friends_List"
        private const val SHARE_DOCUMENT = "Share_List"
        private const val ADDED_YOU: Long = -1
        private const val REMOVED_YOU: Long = -2
    }
}