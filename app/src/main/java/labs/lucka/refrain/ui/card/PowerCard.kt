package labs.lucka.refrain.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun PowerCard(mutable: Boolean) {
    var wakeLockEnabled by rememberPreference(Keys.Power.WakeLock, false)
    Card {
        Column(
            modifier = Modifier
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                stringResource(R.string.power),
                Icons.Filled.Power,
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(
                    stringResource(R.string.power_enable_wake_lock),
                    Icons.Filled.DirectionsWalk,
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(wakeLockEnabled, { checked -> wakeLockEnabled = checked }, enabled = mutable)
            }

            Text(
                stringResource(R.string.power_enable_wake_lock_description),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}