package labs.lucka.refrain.ui.content.settings.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Section(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)) {
        Text(
            title,
            modifier = modifier
                .padding(
                    horizontal = Constants.HorizontalPadding
                ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge
        )
        content()
    }
}