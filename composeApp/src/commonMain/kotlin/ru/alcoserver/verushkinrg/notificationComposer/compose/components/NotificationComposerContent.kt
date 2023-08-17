package ru.alcoserver.verushkinrg.notificationComposer.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerEvent
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.ComposerState
import ru.alcoserver.verushkinrg.notificationComposer.presentation.model.User

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

        OutlinedTextField(
            value = userFilter,
            onValueChange = { onEvent(ComposerEvent.OnUserFilterInput(it)) },
            label = { Text(text = "Users filter") },
            trailingIcon = {
                if (userFilter.isNotBlank()) Image(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.clickable { onEvent(ComposerEvent.ClearUserFilter) })
            },
            shape = CircleShape,
            maxLines = 1,
            singleLine = true
        )

        Spacer(Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape
                )
                .clickable { onEvent(ComposerEvent.OnUsersExpand) }
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)
        ) {
            UserView(
                user = user,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.rotate(if (usersExpanded) 180f else 0f)
            )

            DropdownMenu(
                expanded = usersExpanded,
                onDismissRequest = { onEvent(ComposerEvent.OnUsersExpand) },
                modifier = Modifier.fillMaxWidth()
            ) {
                availableUsers.forEach { user ->
                    DropdownMenuItem(
                        text = { UserView(user) },
                        onClick = { onEvent(ComposerEvent.OnUserTap(user)) }
                    )
                }

                if (availableUsers.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Users not found") },
                        onClick = { onEvent(ComposerEvent.OnUsersExpand) },
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onEvent(ComposerEvent.OnSend) },
            enabled = user != null && title.isNotBlank()
        ) {
            Text(text = "Send")
        }
    }
}

@Composable
fun UserView(
    user: User?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "nick: ${user?.nickName ?: ""}",
            maxLines = 1,
            overflow = TextOverflow.Visible
        )

        Text(
            text = "mail: ${user?.email ?: ""}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Light,
            maxLines = 1,
            overflow = TextOverflow.Visible
        )
    }
}