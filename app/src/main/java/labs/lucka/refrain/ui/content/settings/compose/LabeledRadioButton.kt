package labs.lucka.refrain.ui.content.settings.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

@Composable
fun LabeledRadioButton(text: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            modifier = Modifier
                .padding(
                    start = Constants.HorizontalPadding,
                    top = Constants.ContentSpace / 2,
                    bottom = Constants.ContentSpace / 2
                ),
            style = MaterialTheme.typography.titleMedium
        )
        RadioButton(
            selected,
            null,
            enabled = enabled,
            modifier = Modifier
                .padding(
                    top = Constants.ContentSpace / 2,
                    end = Constants.HorizontalPadding,
                    bottom = Constants.ContentSpace / 2
                )
        )
    }
}