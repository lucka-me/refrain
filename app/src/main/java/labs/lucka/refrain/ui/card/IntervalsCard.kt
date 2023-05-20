package labs.lucka.refrain.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.EditWithDialogField
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun IntervalsCard(mutable: Boolean) {
    var timeInterval by rememberPreference(Keys.Interval.Time, 0)
    var distanceInterval by rememberPreference(Keys.Interval.Distance, 0F)

    ExpandableCard(stringResource(R.string.interval), Icons.Filled.Pending) {
        EditWithDialogField(
            title = stringResource(R.string.interval_time),
            value = timeInterval,
            mutable = mutable,
            description = stringResource(R.string.interval_time_description),
            imageVector = Icons.Filled.Timelapse,
            imageDescription = stringResource(R.string.interval_time_alt),
            labelText = stringResource(R.string.seconds),
            keyboardType = KeyboardType.Number,
            parser = { it.toLongOrNull() }
        ) {
            timeInterval = it
        }

        EditWithDialogField(
            title = stringResource(R.string.interval_distance),
            value = distanceInterval,
            mutable = mutable,
            description = stringResource(R.string.interval_distance_description),
            imageVector = Icons.Filled.Straighten,
            imageDescription = stringResource(R.string.interval_distance_alt),
            labelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal,
            parser = { it.toFloatOrNull() }
        ) {
            distanceInterval = it
        }
    }
}