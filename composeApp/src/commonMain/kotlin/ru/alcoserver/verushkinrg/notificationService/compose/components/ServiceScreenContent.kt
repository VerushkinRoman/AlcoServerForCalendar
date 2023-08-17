package ru.alcoserver.verushkinrg.notificationService.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceEvent
import ru.alcoserver.verushkinrg.notificationService.presentation.model.NotificationServiceState
import ru.alcoserver.verushkinrg.openUrl

@Composable
fun ServiceScreenContent(
    state: () -> NotificationServiceState,
    onEvent: (NotificationServiceEvent) -> Unit,
    modifier: Modifier
) {
    val running by derivedStateOf { state().running }
    val filePickerOpened by derivedStateOf { state().filePickerOpened }
    val servicesPath by derivedStateOf { state().serviceAccountPath }
    val buttonsEnabled by derivedStateOf { servicesPath.isNotEmpty() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PathSection(
            filePickerOpened = { filePickerOpened },
            servicesPath = { servicesPath },
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        MainActionButtons(
            running = { running },
            buttonsEnabled = { buttonsEnabled },
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SecondaryActionButtons(
            buttonsEnabled = { buttonsEnabled },
            onEvent = onEvent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                openUrl("https://console.firebase.google.com/u/1/project/alcocalendar/firestore/data/")
            }
        ) {
            Text("Open firebase console")
        }
    }
}

@Composable
private fun PathSection(
    filePickerOpened: () -> Boolean,
    servicesPath: () -> String,
    onEvent: (NotificationServiceEvent) -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.wrapContentWidth()
    ) {
        Text(
            text = "Services account: ${servicesPath()}",
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .weight(1f)
                .clipToBounds()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { onEvent(NotificationServiceEvent.OpenFilePicker) }
        ) {
            Text(text = "Change")
        }

        FilePicker(filePickerOpened(), fileExtensions = listOf("json")) { file ->
            onEvent(NotificationServiceEvent.CloseFilePicker(path = file?.path ?: ""))
        }
    }
}

@Composable
private fun MainActionButtons(
    running: () -> Boolean,
    buttonsEnabled: () -> Boolean,
    onEvent: (NotificationServiceEvent) -> Unit,
    modifier: Modifier
) {
    Row(modifier = modifier) {
        Button(
            onClick = { onEvent(NotificationServiceEvent.Start) },
            enabled = !running() && buttonsEnabled(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (running()) Color.White else Color.Green
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Start",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { onEvent(NotificationServiceEvent.Stop) },
            enabled = running(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (running()) Color.Red else Color.White
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Stop",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun SecondaryActionButtons(
    buttonsEnabled: () -> Boolean,
    onEvent: (NotificationServiceEvent) -> Unit,
    modifier: Modifier
) {
    Row(modifier = modifier) {
        Button(
            enabled = buttonsEnabled(),
            onClick = { onEvent(NotificationServiceEvent.OpenManageDialog) },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Manage DB",
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            enabled = buttonsEnabled(),
            onClick = { onEvent(NotificationServiceEvent.OpenComposerDialog) },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Open Message Composer",
                maxLines = 1
            )
        }
    }
}