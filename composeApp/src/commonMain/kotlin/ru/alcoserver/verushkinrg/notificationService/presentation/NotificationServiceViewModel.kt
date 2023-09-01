package ru.alcoserver.verushkinrg.notificationService.presentation

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.ListenerRegistration
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.alcoserver.verushkinrg.common.core.di.Inject
import ru.alcoserver.verushkinrg.common.data.DBCleaner
import ru.alcoserver.verushkinrg.common.data.model.NotificationData
import ru.alcoserver.verushkinrg.common.settings.SettingsRepository
import ru.alcoserver.verushkinrg.common.utils.CoroutinesDispatchers
import ru.alcoserver.verushkinrg.notificationService.presentation.messenger.Messenger
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceEvent
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceState
import java.io.FileInputStream
import java.io.InputStream
import java.time.LocalDate
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class NotificationServiceViewModel {
    private val _state = MutableStateFlow(NotificationServiceState())
    val state: StateFlow<NotificationServiceState> = _state.asStateFlow()

    private val settingsRepository: SettingsRepository = Inject.instance()
    private val coroutinesDispatchers: CoroutinesDispatchers = Inject.instance()
    private val viewModelScope: CoroutineScope =
        CoroutineScope(coroutinesDispatchers.io + SupervisorJob())
    private var monitoringJob: Job? = null

    private var notifications: CollectionReference? = null
    private var registration: ListenerRegistration? = null

    init {
        _state.update { it.copy(serviceAccountPath = settingsRepository.serviceAccountPath) }
        prepareFirestore()
    }

    fun onEvent(event: NotificationServiceEvent) {
        when (event) {
            NotificationServiceEvent.Start -> start()
            NotificationServiceEvent.Stop -> stop()
            NotificationServiceEvent.OpenManageDialog -> changeManageDialogState(opened = true)
            NotificationServiceEvent.CloseManageDialog -> changeManageDialogState(opened = false)
            NotificationServiceEvent.OpenComposerDialog -> changeComposerDialogState(opened = true)
            NotificationServiceEvent.CloseComposerDialog -> changeComposerDialogState(opened = false)
            NotificationServiceEvent.CloseErrorMessage -> closeError()
            NotificationServiceEvent.OpenFilePicker -> openFilePicker()
            is NotificationServiceEvent.CloseFilePicker -> closeFilePicker(path = event.path)
        }
    }

    private fun prepareFirestore() {
        val path = state.value.serviceAccountPath
        if (path.isNotEmpty()) {
            val serviceAccount: InputStream = FileInputStream(path)
            val credentials = GoogleCredentials.fromStream(serviceAccount)
            val options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build()

            try {
                FirebaseApp.initializeApp(options)
            } catch (_: IllegalStateException) {
            }
        }
    }

    private fun closeFilePicker(path: String) {
        if (path.isEmpty()) {
            _state.update { it.copy(filePickerOpened = false) }
            return
        }

        _state.update {
            it.copy(
                filePickerOpened = false,
                serviceAccountPath = path
            )
        }

        settingsRepository.serviceAccountPath = path
        prepareFirestore()
    }

    private fun openFilePicker() {
        _state.update { it.copy(filePickerOpened = true) }
    }

    private fun closeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun changeComposerDialogState(opened: Boolean) {
        _state.update { it.copy(composeNotificationDialogOpened = opened) }
    }

    private fun changeManageDialogState(opened: Boolean) {
        _state.update { it.copy(manageDialogOpened = opened) }
    }

    private fun start() {
        monitoringJob?.cancel()

        if (state.value.serviceAccountPath.isEmpty()) {
            showError("Service account path is empty")
            return
        }

        monitoringJob = viewModelScope.launch(coroutinesDispatchers.io) {
            launch {
                while (true) {
                    if (!state.value.running) {
                        watchForDB()
                        _state.update { it.copy(running = true) }
                    }
                    delay(1.minutes)
                }
            }

            launch {
                while (true) {
                    if (settingsRepository.dbCleanupDate
                        < LocalDate.now().minusMonths(1).toEpochDay()
                    ) {
                        try {
                            DBCleaner.cleanDatabase()
                        } catch (e: Exception) {
                            showError(e.message ?: e.toString())
                        }
                    }

                    delay(1.days)
                }
            }
        }
    }

    private fun stop() {
        registration?.remove()
        registration = null
        notifications = null
        monitoringJob?.cancel()
        _state.update { it.copy(running = false) }
    }

    private fun watchForDB() {
        val db = FirestoreClient.getFirestore()
        notifications = db.collection("Notifications")
        registration = notifications?.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                exception.message?.let { showError(it) }
                exception.printStackTrace()
                stop()
                return@addSnapshotListener
            }

            snapshots?.documents?.forEach { documentSnapshot ->
                val notification = documentSnapshot?.toObject(NotificationData::class.java)
                    ?: return@forEach

                Messenger.sendMessage(notification) ?: return@forEach

                notifications?.document(documentSnapshot.id)?.delete()
            }
        }
    }

    private fun showError(error: String) {
        _state.update { it.copy(errorMessage = error) }
    }
}