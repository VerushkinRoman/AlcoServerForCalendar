package ru.alcoserver.verushkinrg.notificationService.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.viewmodel.viewModel
import ru.alcoserver.verushkinrg.common.compose.ErrorDialog
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
        DialogWindow(
            onCloseRequest = { onEvent(NotificationServiceEvent.CloseManageDialog) },
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize.Unspecified
            ),
            title = "Manage DB",
            resizable = false
        ) {
            AppTheme {
                DBManagerScreen(modifier = modifier.padding(16.dp))
            }
        }
    }

    if (composeNotificationDialogOpened) {
        DialogWindow(
            onCloseRequest = { onEvent(NotificationServiceEvent.CloseComposerDialog) },
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize.Unspecified
            ),
            title = "Compose Notification",
            resizable = false
        ) {
            AppTheme {
                NotificationComposerScreen(modifier = modifier.padding(16.dp))
            }
        }
    }

    ErrorDialog(
        errorMessage = errorMessage,
        onClose = { onEvent(NotificationServiceEvent.CloseErrorMessage) }
    )
}