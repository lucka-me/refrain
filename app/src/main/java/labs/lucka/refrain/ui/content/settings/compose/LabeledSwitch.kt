package labs.lucka.refrain.ui.content.settings.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LabeledSwitch(
    text: String,
    checked: Boolean,
    descriptions: String? = null,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = Constants.HorizontalPadding).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1F),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Text(text, style = MaterialTheme.typography.titleMedium)
            if (descriptions != null) {
                Text(
                    descriptions,
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Switch(
            checked,
            onCheckedChange,
            modifier = Modifier.padding(start = Constants.ContentSpace),
            enabled = enabled
        )
    }
}