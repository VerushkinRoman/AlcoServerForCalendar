package ru.alcoserver.verushkinrg.notificationService.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.viewmodel.viewModel
import ru.alcoserver.verushkinrg.common.compose.ErrorDialog
import ru.alcoserver.verushkinrg.common.compose.ThemedWindow
import ru.alcoserver.verushkinrg.dbManager.compose.DBManagerScreen
import ru.alcoserver.verushkinrg.notificationComposer.compose.NotificationComposerScreen
import ru.alcoserver.verushkinrg.notificationService.compose.components.ServiceScreenContent
import ru.alcoserver.verushkinrg.notificationService.presentation.NotificationServiceViewModel
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceEvent
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceState
import ru.alcoserver.verushkinrg.theme.AppTheme

@Composable
fun ServiceScreen(
    modifier: Modifier
) {
    AppTheme {
        val viewModel =
            viewModel(modelClass = NotificationServiceViewModel::class) { NotificationServiceViewModel() }

        val state by viewModel.state.collectAsStateWithLifecycle()

        ServiceScreenContent(
            state = { state },
            onEvent = viewModel::onEvent,
            modifier = modifier.padding(16.dp)
        )

        Dialogs(
            state = { state },
            onEvent = viewModel::onEvent,
            modifier = modifier
        )
    }
}

@Composable
private fun Dialogs(
    state: () -> NotificationServiceState,
    onEvent: (NotificationServiceEvent) -> Unit,
    modifier: Modifier
) {
    val manageDialogOpened by remember { derivedStateOf { state().manageDialogOpened } }
    val composeNotificationDialogOpened by remember { derivedStateOf { state().composeNotificationDialogOpened } }
    val errorMessage by remember { derivedStateOf { state().errorMessage } }

    if (manageDialogOpened) {
        ThemedWindow(
            onCloseRequest = { onEvent(NotificationServiceEvent.CloseManageDialog) },
            title = "Manage DB"
        ) {
            DBManagerScreen(modifier = modifier.padding(16.dp))
        }
    }

    if (composeNotificationDialogOpened) {
        ThemedWindow(
            onCloseRequest = { onEvent(NotificationServiceEvent.CloseComposerDialog) },
            title = "Compose Notification"
        ) {
            NotificationComposerScreen(modifier = modifier.padding(16.dp))
        }
    }

    ErrorDialog(
        errorMessage = errorMessage,
        onClose = { onEvent(NotificationServiceEvent.CloseErrorMessage) }
    )
}