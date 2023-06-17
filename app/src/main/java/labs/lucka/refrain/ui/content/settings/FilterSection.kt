package labs.lucka.refrain.ui.content.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.content.compose.rememberPreference
import labs.lucka.refrain.ui.content.settings.compose.EditWithDialogField
import labs.lucka.refrain.ui.content.settings.compose.Section

@Composable
fun FilterSection(mutable: Boolean) {
    Section(stringResource(R.string.filter)) {
        var accuracyFilter by rememberPreference(Keys.Filter.Accuracy, 0F)
        EditWithDialogField(
            stringResource(R.string.filter_accuracy),
            accuracyFilter,
            { it.toFloatOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.filter_accuracy_description),
            editorLabelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal
        ) {
            accuracyFilter = it
        }
    }
}