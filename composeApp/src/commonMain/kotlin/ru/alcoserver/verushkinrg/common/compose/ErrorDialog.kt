package ru.alcoserver.verushkinrg.common.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import ru.alcoserver.verushkinrg.theme.AppTheme

@Composable
fun ErrorDialog(
    errorMessage: String?,
    onClose: () -> Unit
) {
    errorMessage?.let { message ->
        DialogWindow(
            onCloseRequest = onClose,
            state = rememberDialogState(
                position = WindowPosition(Alignment.Center),
                size = DpSize.Unspecified,
            ),
            title = "Error",
            resizable = false
        ) {
            AppTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .padding(16.dp)
                ) {
                    Text(text = message)

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = onClose
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}