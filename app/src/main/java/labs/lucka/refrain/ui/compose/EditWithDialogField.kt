package labs.lucka.refrain.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> EditWithDialogField(
    title: String,
    value: T,
    mutable: Boolean,
    description: String,
    imageVector: ImageVector,
    imageDescription: String,
    labelText: String,
    keyboardType: KeyboardType,
    parser: (String) -> T?,
    onValueSet: (T) -> Unit,
) {
    var showingDialog: Boolean by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = mutable, role = Role.Button) { showingDialog = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(
            text = title,
            imageVector = imageVector,
            imageDescription = imageDescription,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1F))
        Text(text = value.toString())
        IconButton(onClick = { showingDialog = true }, enabled = mutable) {
            Icon(
                Icons.Filled.Edit,
                "Edit",
                modifier = Modifier.size(with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() })
            )
        }
    }
    if (showingDialog) {
        var valueText by remember { mutableStateOf(value.toString()) }
        var valid: Boolean by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showingDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newValue = parser(valueText)
                        if (newValue != null) {
                            onValueSet(newValue)
                        }
                        showingDialog = false
                    },
                    enabled = valid
                ) {
                    Text("Confirm")
                }
            },
            icon = { Icon(imageVector, imageDescription) },
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(description)
                    TextField(
                        value = valueText,
                        onValueChange = {
                            valueText = it
                            valid = parser(valueText) != null
                        },
                        label = { Text(labelText) },
                        isError = !valid,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        keyboardActions = KeyboardActions(onDone = {
                            val newValue = parser(valueText)
                            if (newValue != null) {
                                onValueSet(newValue)
                            }
                            showingDialog = false
                        }),
                        singleLine = true
                    )
                }
            }
        )
    }
}