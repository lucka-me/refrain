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
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TracingStatusCard(tracing: Boolean, onToggle: () -> Unit) {
    val cardColors = if (tracing) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors()
    }
    Card(onClick = onToggle, colors = cardColors) {
        Column(modifier = Modifier.padding(all = Constants.CardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val textStyle = MaterialTheme.typography.titleLarge
                Text(
                    stringResource(if (tracing) R.string.tracing_status_tracing else R.string.tracing_status_ready),
                    style = textStyle
                )
                Icon(
                    if (tracing) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                    stringResource(if (tracing) R.string.stop else R.string.start),
                    modifier = Modifier.size(
                        with(LocalDensity.current) { textStyle.fontSize.toDp() }
                    )
                )
            }
        }
    }
}