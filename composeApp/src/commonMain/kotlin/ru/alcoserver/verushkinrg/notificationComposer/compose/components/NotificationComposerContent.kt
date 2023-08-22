package ru.alcoserver.verushkinrg.notificationComposer.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import ru.alcoserver.verushkinrg.common.compose.UsersChooseBlock
import ru.alcoserver.verushkinrg.common.data.model.User
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerEvent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerState

@Composable
fun NotificationComposerContent(
    state: () -> ComposerState,
    onEvent: (ComposerEvent) -> Unit,
    modifier: Modifier
) {
    val availableUsers by remember { derivedStateOf { state().availableUsers } }
    val usersExpanded by remember { derivedStateOf { state().usersExpanded } }
    val user: User? by remember { derivedStateOf { state().user } }
    val userFilter: String by remember { derivedStateOf { state().userFilter } }
    val title: String by remember { derivedStateOf { state().title } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { onEvent(ComposerEvent.OnTitleInput(it)) },
            label = { Text(text = "Enter Message") },
            supportingText = { Text(text = "${title.length}/70(98)") },
            isError = title.length > 70,
            trailingIcon = {
                if (title.isNotBlank()) Image(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.clickable { onEvent(ComposerEvent.OnTitleClear) })
            },
            shape = RoundedCornerShape(28.dp),
            maxLines = 4,
            minLines = 4
        )

        Spacer(Modifier.height(8.dp))

        UsersChooseBlock(
            availableUsers = availableUsers,
            filter = userFilter,
            onFilterChange = { onEvent(ComposerEvent.OnUserFilterInput(it)) },
            onFilterClear = { onEvent(ComposerEvent.ClearUserFilter) },
            onUsersExpand = { onEvent(ComposerEvent.OnUsersExpand) },
            user = user,
            usersExpanded = usersExpanded,
            onUserTap = { onEvent(ComposerEvent.OnUserTap(it)) }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onEvent(ComposerEvent.OnSend) },
            enabled = user != null && title.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}

