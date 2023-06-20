package labs.lucka.refrain.ui.content.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.content.compose.Label
import labs.lucka.refrain.ui.content.main.compose.Constants

@Composable
fun LocationPermissionCard(onRequest: () -> Unit) {
    OutlinedCard {
        Column(
            modifier = Modifier
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                text = stringResource(R.string.location_permission),
                imageVector = Icons.Filled.Lock,
                style = MaterialTheme.typography.titleLarge
            )
            Text(stringResource(R.string.location_permission_description))
            TextButton(onRequest) {
                Text(stringResource(R.string.location_permission_request))
            }
        }
    }
}