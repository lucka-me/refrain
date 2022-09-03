package labs.lucka.refrain.ui.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun OutputFormatCard(mutable: Boolean) {
    var enableCSV by rememberPreference(Keys.outputFormat.csv, true)
    var enableGPX by rememberPreference(Keys.outputFormat.gpx, false)
    var enableKML by rememberPreference(Keys.outputFormat.kml, false)

    ExpandableCard(title = "Format", imageVector = Icons.Filled.InsertDriveFile, imageDescription = "Output Format") {
        FormatField("CSV", enableCSV, mutable) { enableCSV = !enableCSV }
        FormatField("GPX", enableGPX, mutable) { enableGPX = !enableGPX }
        FormatField("KML", enableKML, mutable) { enableKML = !enableKML }
    }
}

@Composable
fun FormatField(name: String, enabled: Boolean, mutable: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(enabled, mutable, Role.Checkbox, onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(enabled, onCheckedChange = null, enabled = mutable)
        Text(name, style = MaterialTheme.typography.bodyLarge)
    }
}