package labs.lucka.refrain.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun Label(
    text: String,
    imageVector: ImageVector,
    imageDescription: String = text,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Label(imageVector = imageVector, imageDescription = imageDescription, style = style) {
        Text(text, style = style)
    }
}

@Composable
fun Label(
    imageVector: ImageVector,
    imageDescription: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    text: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector,
            imageDescription,
            modifier = Modifier.size(with(LocalDensity.current) { style.fontSize.toDp() })
        )
        text()
    }
}