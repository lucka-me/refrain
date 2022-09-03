package labs.lucka.refrain.ui.card

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TracingStatusCard(tracing: Boolean, onToggle: () -> Unit) {
    Card(
        onClick = onToggle
    ) {
        Column(modifier = Modifier.padding(all = Constants.CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textStyle = MaterialTheme.typography.titleLarge
                Text(
                    text = if (tracing) "Tracing" else "Ready",
                    style = textStyle
                )
                Icon(
                    if (tracing) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    if (tracing) "Stop" else "Start",
                    modifier = Modifier.size(
                        with(LocalDensity.current) { textStyle.fontSize.toDp() }
                    )
                )
            }
        }
    }
}