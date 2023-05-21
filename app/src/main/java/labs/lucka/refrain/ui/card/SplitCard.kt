package labs.lucka.refrain.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.EditWithDialogField
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun SplitCard(mutable: Boolean) {
    var timeInterval by rememberPreference(Keys.Split.Time, 0)
    var distanceInterval by rememberPreference(Keys.Split.Distance, 0F)

    ExpandableCard(stringResource(R.string.split_card_title), Icons.Filled.ContentCut) {
        EditWithDialogField(
            title = stringResource(R.string.split_card_time),
            value = timeInterval,
            mutable = mutable,
            description = stringResource(R.string.split_card_time_description),
            imageVector = Icons.Filled.Timelapse,
            imageDescription = stringResource(R.string.split_card_time_alt),
            labelText = stringResource(R.string.seconds),
            keyboardType = KeyboardType.Number,
            parser = { it.toLongOrNull() }
        ) {
            timeInterval = it
        }

        EditWithDialogField(
            title = stringResource(R.string.split_card_distance),
            value = distanceInterval,
            mutable = mutable,
            description = stringResource(R.string.split_card_distance_description),
            imageVector = Icons.Filled.Straighten,
            imageDescription = stringResource(R.string.split_card_distance_alt),
            labelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal,
            parser = { it.toFloatOrNull() }
        ) {
            distanceInterval = it
        }
    }
}