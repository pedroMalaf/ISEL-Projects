package isel.leic.tds.checkers.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import isel.leic.tds.checkers.model.Name

enum class Action(val text: String) {
    START("Start"),
    REFRESH("Refresh"),
}

@Composable
fun ClashNameEdit(
    action: Action,
    onCancel: () -> Unit,
    onAction: (Name) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        title = {
            Text(
                "Clash to ${action.text}",
                style = MaterialTheme.typography.h5
            )
        },
        onDismissRequest = { },
        confirmButton = {
            TextButton(
                enabled = Name.isValid(name),
                onClick = { onAction(Name(name)) },
            ) { Text(Action.START.text) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        },
        text = {
            OutlinedTextField(name,
                onValueChange = { name = it },
                label= { Text("Insert name of the clash") }
            )
        }
    )
}