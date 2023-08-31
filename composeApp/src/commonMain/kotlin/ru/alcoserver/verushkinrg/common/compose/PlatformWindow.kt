package ru.alcoserver.verushkinrg.common.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import ru.alcoserver.verushkinrg.theme.AppTheme

@Composable
fun ThemedWindow(
    onCloseRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) {
    DialogWindow(
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize.Unspecified
        ),
        title = title,
        resizable = false
    ) {
        AppTheme {
            content()
        }
    }
}