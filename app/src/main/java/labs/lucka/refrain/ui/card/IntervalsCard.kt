package labs.lucka.refrain.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.EditWithDialogField
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun IntervalsCard(mutable: Boolean) {
    var timeInterval by rememberPreference(Keys.interval.time, 0)
    var distanceInterval by rememberPreference(Keys.interval.distance, 0F)

    ExpandableCard(title = "Intervals", imageVector = Icons.Filled.Pending, imageDescription = "Intervals") {
        EditWithDialogField(
            title = "Time",
            value = timeInterval,
            mutable = mutable,
            description = "Set minimum time interval.",
            imageVector = Icons.Filled.Timelapse,
            imageDescription = "Time interval",
            labelText = "Seconds",
            keyboardType = KeyboardType.Number,
            parser = { it.toLongOrNull() }
        ) {
            timeInterval = it
        }

        EditWithDialogField(
            title = "Distance",
            value = distanceInterval,
            mutable = mutable,
            description = "Set minimum distance interval.",
            imageVector = Icons.Filled.Straighten,
            imageDescription = "Distance interval",
            labelText = "Meter",
            keyboardType = KeyboardType.Decimal,
            parser = { it.toFloatOrNull() }
        ) {
            distanceInterval = it
        }
    }
}