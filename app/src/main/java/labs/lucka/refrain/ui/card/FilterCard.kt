package labs.lucka.refrain.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.EditWithDialogField
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun FilterCard(mutable: Boolean) {
    var accuracyFilter by rememberPreference(Keys.filter.accuracy, 0F)
    ExpandableCard(title = "Filter", imageVector = Icons.Filled.FilterAlt, imageDescription = "Filter") {
        EditWithDialogField(
            title = "Accuracy",
            value = accuracyFilter,
            mutable = mutable,
            description = "Set maximum accuracy",
            imageVector = Icons.Filled.Adjust,
            imageDescription = "Accuracy filter",
            labelText = "Meter",
            keyboardType = KeyboardType.Decimal,
            parser = { it.toFloatOrNull() }
        ) {
            accuracyFilter = it
        }
    }
}