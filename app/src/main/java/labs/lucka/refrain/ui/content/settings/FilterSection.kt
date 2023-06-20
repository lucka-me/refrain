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
import labs.lucka.refrain.ui.content.settings.compose.LabeledSwitch
import labs.lucka.refrain.ui.content.settings.compose.Section

@Composable
fun FilterSection(mutable: Boolean) {
    Section(stringResource(R.string.settings_filter)) {
        var accuracyFilter by rememberPreference(Keys.Filter.Accuracy, 0F)
        var ignoreDuplicated by rememberPreference(Keys.Filter.IgnoreDuplicated, false)
        EditWithDialogField(
            stringResource(R.string.settings_filter_accuracy),
            accuracyFilter,
            { it.toFloatOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.settings_filter_accuracy_description),
            editorLabelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal
        ) {
            accuracyFilter = it
        }

        LabeledSwitch(
            stringResource(R.string.settings_filter_ignore_duplicated),
            ignoreDuplicated,
            descriptions = stringResource(R.string.settings_filter_ignore_duplicated_description),
            enabled = mutable
        ) { checked ->
            ignoreDuplicated = checked
        }
    }
}