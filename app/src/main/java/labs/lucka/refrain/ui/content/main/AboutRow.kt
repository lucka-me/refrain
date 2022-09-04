package labs.lucka.refrain.ui.content.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.BuildConfig
import labs.lucka.refrain.R

@Composable
fun AboutRow() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var showAuthor by remember { mutableStateOf(true) }
        val sourceCodeIntent = remember {
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lucka-me/refrain"))
        }
        Text(
            stringResource(R.string.source_code),
            modifier = Modifier.clickable { context.startActivity(sourceCodeIntent) },
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            "${BuildConfig.VERSION_NAME}-${BuildConfig.BUILD_TYPE} (${BuildConfig.VERSION_CODE})",
            color = MaterialTheme.colorScheme.outline,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            if (showAuthor) "By Lucka" else "Namioto",
            modifier = Modifier.clickable { showAuthor = !showAuthor },
            color = if (showAuthor) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.bodySmall
        )
    }
}