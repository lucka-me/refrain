package labs.lucka.refrain.ui.content.settings.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.R

@Composable
fun <T> EditWithDialogField(
    title: String,
    value: T,
    parser: (String) -> T?,
    enabled: Boolean = true,
    editorDescription: String? = null,
    editorLabelText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueSet: (T) -> Unit,
) {
    var showingDialog: Boolean by remember { mutableStateOf(false) }

    ClickableItem(title, description = value.toString(), enabled = enabled) { showingDialog = true }

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
                    Text(stringResource(R.string.confirm))
                }
            },
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (editorDescription != null) {
                        Text(editorDescription)
                    }
                    TextField(
                        value = valueText,
                        onValueChange = {
                            valueText = it
                            valid = parser(valueText) != null
                        },
                        label = if (editorLabelText != null) { { Text(editorLabelText) } } else null,
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