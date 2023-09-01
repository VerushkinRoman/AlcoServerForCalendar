package ru.alcoserver.verushkinrg.notificationComposer.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.alcoserver.verushkinrg.common.compose.ErrorDialog
import ru.alcoserver.verushkinrg.notificationComposer.compose.components.NotificationComposerContent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.ComposerViewModel
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerEvent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerState

@Composable
fun NotificationComposerScreen(
    modifier: Modifier
) {
    val viewModel = remember { ComposerViewModel() }
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        viewModel.onEvent(ComposerEvent.OnScreenEnter)

        onDispose {
            viewModel.onEvent(ComposerEvent.OnScreenExit)
        }
    }

    NotificationComposerContent(
        state = { state },
        onEvent = viewModel::onEvent,
        modifier = modifier
    )

    Dialogs(
        state = { state },
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun Dialogs(
    state: () -> ComposerState,
    onEvent: (ComposerEvent) -> Unit
) {
    val errorMessage by remember { derivedStateOf { state().errorMessage } }

    ErrorDialog(
        errorMessage = errorMessage,
        onClose = { onEvent(ComposerEvent.OnCloseError) }
    )
}