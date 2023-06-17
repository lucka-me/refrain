package labs.lucka.refrain.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.LabeledSwitch
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun PowerCard(mutable: Boolean) {
    var keepScreenOn by rememberPreference(Keys.Power.KeepScreenOn, false)
    var wakeLockEnabled by rememberPreference(Keys.Power.WakeLock, false)
    val currentView = LocalView.current
    OutlinedCard {
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

            LabeledSwitch(
                stringResource(R.string.power_enable_wake_lock),
                Icons.Filled.DirectionsWalk,
                wakeLockEnabled, enabled = mutable
            ) { checked ->
                wakeLockEnabled = checked
            }

            Text(
                stringResource(R.string.power_enable_wake_lock_description),
                style = MaterialTheme.typography.bodySmall
            )

            Divider()

            LabeledSwitch(stringResource(R.string.power_enable_keep_screen_on), Icons.Filled.LightMode, keepScreenOn) { checked ->
                keepScreenOn = checked
                currentView.keepScreenOn = keepScreenOn
            }
        }
    }
}