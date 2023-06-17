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
fun IntervalSection(mutable: Boolean) {
    var timeInterval by rememberPreference(Keys.Interval.Time, 0)
    var distanceInterval by rememberPreference(Keys.Interval.Distance, 0F)
    Section(stringResource(R.string.interval)) {
        EditWithDialogField(
            stringResource(R.string.interval_time),
            timeInterval,
            { it.toLongOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.interval_time_description),
            editorLabelText = stringResource(R.string.seconds),
            keyboardType = KeyboardType.Number
        ) {
            timeInterval = it
        }

        EditWithDialogField(
            stringResource(R.string.interval_distance),
            distanceInterval,
            { it.toFloatOrNull() },
            enabled = mutable,
            editorDescription = stringResource(R.string.interval_distance_description),
            editorLabelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal
        ) {
            distanceInterval = it
        }
    }
}