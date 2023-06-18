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
fun SplitSection(mutable: Boolean) {
    var timeInterval by rememberPreference(Keys.Split.Time, 0)
    var distanceInterval by rememberPreference(Keys.Split.Distance, 0F)
    Section(stringResource(R.string.settings_split)) {
        EditWithDialogField(
            stringResource(R.string.settings_split_time),
            timeInterval,
            { it.toLongOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.settings_split_time_description),
            editorLabelText = stringResource(R.string.seconds),
            keyboardType = KeyboardType.Number
        ) {
            timeInterval = it
        }

        EditWithDialogField(
            stringResource(R.string.settings_split_distance),
            distanceInterval,
            { it.toFloatOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.settings_split_distance_description),
            editorLabelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal
        ) {
            distanceInterval = it
        }
    }
}