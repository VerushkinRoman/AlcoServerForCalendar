package ru.alcoserver.verushkinrg.dbManager.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.alcoserver.verushkinrg.common.compose.UsersChooseBlock
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerEvent
import ru.alcoserver.verushkinrg.dbManager.presentation.model.DBManagerState

@Composable
fun DBManagerContent(
    state: () -> DBManagerState,
    onEvent: (DBManagerEvent) -> Unit,
    modifier: Modifier
) {
    val working by remember { derivedStateOf { state().working } }
    val availableUsers1 by remember { derivedStateOf { state().availableUsers1 } }
    val availableUsers2 by remember { derivedStateOf { state().availableUsers2 } }
    val usersExpanded1 by remember { derivedStateOf { state().usersExpanded1 } }
    val usersExpanded2 by remember { derivedStateOf { state().usersExpanded2 } }
    val user1 by remember { derivedStateOf { state().user1 } }
    val user2 by remember { derivedStateOf { state().user2 } }
    val userFilter1 by remember { derivedStateOf { state().userFilter1 } }
    val userFilter2 by remember { derivedStateOf { state().userFilter2 } }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(DBManagerEvent.ClearDB) },
                enabled = !working,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "ClearDB")
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (working) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ButtonDefaults.MinHeight)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row {
            UsersChooseBlock(
                availableUsers = availableUsers1,
                filter = userFilter1,
                onFilterChange = { onEvent(DBManagerEvent.OnUser1FilterInput(it)) },
                onFilterClear = { onEvent(DBManagerEvent.ClearUser1Filter) },
                onUsersExpand = { onEvent(DBManagerEvent.OnUsers1Expand) },
                user = user1,
                usersExpanded = usersExpanded1,
                onUserTap = { onEvent(DBManagerEvent.OnUser1Tap(it)) },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            UsersChooseBlock(
                availableUsers = availableUsers2,
                filter = userFilter2,
                onFilterChange = { onEvent(DBManagerEvent.OnUser2FilterInput(it)) },
                onFilterClear = { onEvent(DBManagerEvent.ClearUser2Filter) },
                onUsersExpand = { onEvent(DBManagerEvent.OnUsers2Expand) },
                user = user2,
                usersExpanded = usersExpanded2,
                onUserTap = { onEvent(DBManagerEvent.OnUser2Tap(it)) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        Row {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = { onEvent(DBManagerEvent.MakeUsersFriends) },
                    enabled = user1 != null && user2 != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text(text = "Friend users")
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Button(
                    onClick = { onEvent(DBManagerEvent.UnfriendUsers) },
                    enabled = user1 != null && user2 != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Unfriend users")
                }
            }
        }
    }
}

