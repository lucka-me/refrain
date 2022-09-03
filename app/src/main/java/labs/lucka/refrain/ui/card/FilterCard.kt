package labs.lucka.refrain.ui.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.FilterAlt
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
fun FilterCard(mutable: Boolean) {
    var accuracyFilter by rememberPreference(Keys.filter.accuracy, 0F)
    ExpandableCard(stringResource(R.string.filter), Icons.Filled.FilterAlt) {
        EditWithDialogField(
            title = stringResource(R.string.filter_accuracy),
            value = accuracyFilter,
            mutable = mutable,
            description = stringResource(R.string.filter_accuracy_description),
            imageVector = Icons.Filled.Adjust,
            imageDescription = stringResource(R.string.filter_accuracy_alt),
            labelText = stringResource(R.string.meter),
            keyboardType = KeyboardType.Decimal,
            parser = { it.toFloatOrNull() }
        ) {
            accuracyFilter = it
        }
    }
}