package labs.lucka.refrain.ui.card

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun OutputPathCard(mutable: Boolean) {
    val context = LocalContext.current
    var path by rememberPreference(Keys.outputPath, "")
    val documentTreeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        path = uri.toString()
    }
    Card {
        Column(
            modifier = Modifier
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                text = "Output Path",
                imageVector = Icons.Filled.Folder,
                imageDescription = "Output Path",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = path.ifEmpty { "Please selected a folder to where Refrain saves the trace files." })
            if (mutable) {
                TextButton(onClick = { documentTreeLauncher.launch(Uri.parse(path)) }) {
                    Text(text = if (path.isEmpty()) "Select" else "Change")
                }
            }
        }
    }
}