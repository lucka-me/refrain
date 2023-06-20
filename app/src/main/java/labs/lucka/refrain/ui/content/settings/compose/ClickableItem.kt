package labs.lucka.refrain.ui.content.settings.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun ClickableItem(title: String, enabled: Boolean = true, description: String? = null, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
    ) {
        Text(
            title,
            modifier = Modifier
                .padding(
                    start = Constants.HorizontalPadding,
                    top = Constants.ContentSpace / 2,
                    end = Constants.HorizontalPadding,
                    bottom = if (description != null) 0.dp else Constants.HorizontalPadding
                ),
            style = MaterialTheme.typography.titleMedium
        )
        if (description != null) {
            Text(
                description,
                modifier = Modifier
                    .padding(
                        start = Constants.HorizontalPadding,
                        end = Constants.HorizontalPadding,
                        bottom = Constants.ContentSpace / 2
                    ),
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}