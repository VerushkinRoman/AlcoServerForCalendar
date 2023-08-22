package ru.alcoserver.verushkinrg.common.compose

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.alcoserver.verushkinrg.common.data.model.User

@Composable
fun UsersChooseBlock(
    availableUsers: List<User>,
    filter: String,
    onFilterChange: (String) -> Unit,
    onFilterClear: () -> Unit,
    onUsersExpand: () -> Unit,
    user: User?,
    usersExpanded: Boolean,
    onUserTap: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = filter,
            onValueChange = onFilterChange,
            label = { Text(text = "Users filter") },
            trailingIcon = {
                if (filter.isNotBlank()) Image(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.clickable(onClick = onFilterClear)
                )
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
                .clickable(onClick = onUsersExpand)
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
                onDismissRequest = onUsersExpand,
                modifier = Modifier.fillMaxWidth()
            ) {
                availableUsers.forEach { user ->
                    DropdownMenuItem(
                        text = { UserView(user) },
                        onClick = { onUserTap(user) }
                    )
                }

                if (availableUsers.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Users not found") },
                        onClick = onUsersExpand,
                    )
                }
            }
        }
    }
}

@Composable
private fun UserView(
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