package labs.lucka.refrain.ui.card

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
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
                text = stringResource(R.string.location_permission),
                imageVector = Icons.Filled.Lock,
                style = MaterialTheme.typography.titleLarge
            )
            Text(stringResource(R.string.location_permission_description))
            TextButton(
                onClick = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                }
            ) {
                Text(stringResource(R.string.location_permission_request))
            }
        }
    }
}