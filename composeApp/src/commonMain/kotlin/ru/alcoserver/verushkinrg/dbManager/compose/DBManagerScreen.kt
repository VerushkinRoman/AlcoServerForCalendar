package ru.alcoserver.verushkinrg.dbManager.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.alcoserver.verushkinrg.common.compose.ErrorDialog
import ru.alcoserver.verushkinrg.dbManager.compose.components.DBManagerContent
import ru.alcoserver.verushkinrg.dbManager.presentation.DBManagerViewModel
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerEvent
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerState

@Composable
fun DBManagerScreen(
    modifier: Modifier
) {
    val viewModel = remember { DBManagerViewModel() }
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        viewModel.onEvent(DBManagerEvent.OnScreenEnter)

        onDispose {
            viewModel.onEvent(DBManagerEvent.OnScreenExit)
        }
    }

    DBManagerContent(
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
    state: () -> DBManagerState,
    onEvent: (DBManagerEvent) -> Unit
) {
    val errorMessage by remember { derivedStateOf { state().errorMessage } }

    ErrorDialog(
        errorMessage = errorMessage,
        onClose = { onEvent(DBManagerEvent.OnCloseError) }
    )
}