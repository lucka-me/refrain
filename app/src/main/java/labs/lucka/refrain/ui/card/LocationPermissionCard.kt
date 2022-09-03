package labs.lucka.refrain.ui.card

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import labs.lucka.refrain.ui.compose.Label

@Composable
fun LocationPermissionCard(onResult: (Map<String, Boolean>) -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(), onResult
    )
    Card {
        Column(
            modifier = Modifier.padding(all = Constants.CardPadding).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                text = "Location Permission",
                imageVector = Icons.Filled.Lock,
                imageDescription = "Location Permission",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Refrain requires the permission to access high accuracy locations.")
            TextButton(
                onClick = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                }
            ) {
                Text(text = "Request")
            }
        }
    }
}