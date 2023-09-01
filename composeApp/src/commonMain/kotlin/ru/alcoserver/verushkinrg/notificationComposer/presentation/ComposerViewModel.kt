package ru.alcoserver.verushkinrg.notificationComposer.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.data.NotificationManager
import ru.alcoserver.verushkinrg.common.data.UsersRepo
import ru.alcoserver.verushkinrg.common.data.model.NotificationData
import ru.alcoserver.verushkinrg.common.data.model.User
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchers
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerEvent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerState
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class ComposerViewModel {
    private val _state: MutableStateFlow<ComposerState> = MutableStateFlow(ComposerState())
    val state: StateFlow<ComposerState> = _state.asStateFlow()

    private val coroutinesDispatchers: CoroutinesDispatchers = Inject.instance()
    private var users: List<User> = emptyList()
    private val viewModelScope: CoroutineScope =
        CoroutineScope(coroutinesDispatchers.io + SupervisorJob())

    fun onEvent(event: ComposerEvent) {
        when (event) {
            is ComposerEvent.OnScreenEnter -> updateUsers()
            is ComposerEvent.OnScreenExit -> onCleared()
            is ComposerEvent.OnUserFilterInput -> filterUsers(filter = event.filter)
            is ComposerEvent.ClearUserFilter -> clearUserFilter()
            is ComposerEvent.OnUsersExpand -> expandUsers()
            is ComposerEvent.OnUserTap -> onUserSelected(user = event.user)
            is ComposerEvent.OnTitleInput -> changeTitle(text = event.title)
            is ComposerEvent.OnTitleClear -> changeTitle(text = "")
            is ComposerEvent.OnSend -> sendNotification()
            is ComposerEvent.OnCloseError -> closeError()
        }
    }

    private fun sendNotification() {
        val id = state.value.user?.id ?: return

        viewModelScope.launch {
            val extendedTitle =
                state.value.title + buildString {
                    (0..500).forEach { _ -> append(" ") }
                    (0..4).forEach { _ -> append("\n") }
                }

            try {
                NotificationManager.sendNotification(
                    NotificationData(
                        to = id,
                        title = extendedTitle,
                        message = LocalDate.now().toEpochDay().toString()
                    )
                )
            } catch (e: Exception) {
                showError(e.message ?: e.toString())
            }
        }
    }

    private fun changeTitle(text: String) {
        _state.update { it.copy(title = text) }
    }

    private fun onUserSelected(user: User) {
        _state.update {
            it.copy(
                user = user,
                usersExpanded = false
            )
        }
    }

    private fun expandUsers() {
        val expanded = state.value.usersExpanded
        _state.update { it.copy(usersExpanded = !expanded) }
    }

    private fun filterUsers(filter: String) {
        val filteredUsers = users.filter {
            filter.lowercase() in it.email.lowercase() || filter in it.nickName.lowercase()
        }

        _state.update {
            it.copy(
                userFilter = filter,
                availableUsers = filteredUsers
            )
        }
    }

    private fun updateUsers() {
        viewModelScope.launch(coroutinesDispatchers.io) {
            val newUsers = try {
                UsersRepo.getUsers()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                else {
                    showError(e.message ?: e.toString())
                    emptyList()
                }
            }
            users = newUsers
            _state.update { it.copy(availableUsers = newUsers) }
        }
    }

    private fun clearUserFilter() {
        _state.update {
            it.copy(
                userFilter = "",
                availableUsers = users
            )
        }
    }

    private fun showError(message: String) {
        _state.update { it.copy(errorMessage = message) }
    }

    private fun closeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun onCleared() {
        viewModelScope.coroutineContext.cancelChildren()
    }
}