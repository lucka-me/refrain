package labs.lucka.refrain.ui.card

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.stringResource
import androidx.documentfile.provider.DocumentFile
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference
import java.net.URLDecoder

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
                stringResource(R.string.output_path),
                Icons.Filled.Folder,
                style = MaterialTheme.typography.titleLarge
            )
            if (path.isEmpty()) {
                Text(text = stringResource(R.string.output_path_description))
            } else {
                val decodedPath = URLDecoder.decode(path, "UTF-8")
                Text(text = decodedPath.substring(decodedPath.lastIndexOf(':') + 1))
            }
            Row {
                if (path.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            val tree = DocumentFile.fromTreeUri(context, Uri.parse(path)) ?: return@TextButton
                            val intent = Intent(Intent.ACTION_VIEW, tree.uri)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(stringResource(R.string.output_path_open))
                    }
                }
                if (mutable) {
                    TextButton(onClick = { documentTreeLauncher.launch(Uri.parse(path)) }) {
                        Text(
                            stringResource(if (path.isEmpty()) R.string.output_path_select else R.string.output_path_change)
                        )
                    }
                }
            }
        }
    }
}