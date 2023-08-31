package ru.alcoserver.verushkinrg.common.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    errorMessage: String?,
    onClose: () -> Unit
) {
    errorMessage?.let { message ->
        ThemedWindow(
            onCloseRequest = onClose,
            title = "Error"
        ) {
            Text(
                text = message,
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .padding(16.dp)
            )
        }
    }
}