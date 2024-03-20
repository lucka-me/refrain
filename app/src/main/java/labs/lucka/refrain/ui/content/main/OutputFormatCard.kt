package labs.lucka.refrain.ui.content.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.content.compose.ExpandableCard
import labs.lucka.refrain.ui.content.compose.rememberPreference

@Composable
fun OutputFormatCard(mutable: Boolean) {
    var enableCSV by rememberPreference(Keys.OutputFormat.CSV, true)
    var enableGPX by rememberPreference(Keys.OutputFormat.GPX, false)
    var enableKML by rememberPreference(Keys.OutputFormat.KML, false)

    ExpandableCard(
        stringResource(R.string.output_format),
        Icons.AutoMirrored.Filled.InsertDriveFile,
        stringResource(R.string.output_format_alt)
    ) {
        FormatField(R.string.output_format_csv, enableCSV, mutable) { enableCSV = !enableCSV }
        FormatField(R.string.output_format_gpx, enableGPX, mutable) { enableGPX = !enableGPX }
        FormatField(R.string.output_format_kml, enableKML, mutable) { enableKML = !enableKML }
    }
}

@Composable
fun FormatField(nameId: Int, enabled: Boolean, mutable: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(enabled, mutable, Role.Checkbox, onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(enabled, onCheckedChange = null, enabled = mutable)
        Text(stringResource(nameId), style = MaterialTheme.typography.bodyLarge)
    }
}