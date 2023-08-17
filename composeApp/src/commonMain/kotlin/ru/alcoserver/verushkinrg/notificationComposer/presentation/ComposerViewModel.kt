package ru.alcoserver.verushkinrg.notificationComposer.presentation

import com.google.cloud.firestore.ListenerRegistration
import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchers
import ru.alcoserver.verushkinrg.common.utils.toDataClass
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerEvent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerState
import ru.alcoserver.verushkinrg.common.presentation.model.FirebaseUser
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.NotificationData
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.User
import ru.alcoserver.verushkinrg.common.presentation.model.toUser
import java.time.LocalDate

class ComposerViewModel : ViewModel() {
    private val _state: MutableStateFlow<ComposerState> = MutableStateFlow(ComposerState())
    val state: StateFlow<ComposerState> = _state.asStateFlow()

    private val coroutinesDispatchers: CoroutinesDispatchers = Inject.instance()

    private var usersJob: Job? = null
    private var users: List<User> = emptyList()
    private var registration: ListenerRegistration? = null
    private val notifications = FirestoreClient.getFirestore().collection("Notifications")
    private val usersDocuments = FirestoreClient.getFirestore()
        .collection("Collection_of_all_users")
        .document("Users")

    fun onEvent(event: ComposerEvent) {
        when (event) {
            is ComposerEvent.OnScreenEnter -> startUsersJob()
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
                state.value.title + buildString { (0..4).forEach { _ -> append("\n") } }

            val addedDocRef = notifications.document()
            addedDocRef.set(
                NotificationData(
                    to = id,
                    title = extendedTitle,
                    message = LocalDate.now().toEpochDay().toString()
                )
            )

            // TODO check connection
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

    private fun startUsersJob() {
        if (usersJob?.isActive == true) return

        usersJob = viewModelScope.launch(coroutinesDispatchers.io) {
            registration = usersDocuments.addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    exception.printStackTrace()
                    usersJob?.cancel()
                    return@addSnapshotListener
                }

                val newUsers = mutableListOf<User>()
                documentSnapshot?.data?.values?.forEach { userData ->
                    @Suppress("UNCHECKED_CAST")
                    (userData as? Map<String, Any>)
                        ?.toDataClass<FirebaseUser>()
                        ?.toUser()
                        ?.also { newUsers.add(it) }
                }

                users = newUsers
                if (state.value.availableUsers.isEmpty()) _state.update { it.copy(availableUsers = newUsers) }
            }
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

    private fun showError() {
        _state.update { it.copy(errorMessage = "Error sending notification") }
    }

    private fun closeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        _state.update { ComposerState() }
        registration?.remove()
    }
}