package labs.lucka.refrain.ui.content.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.content.compose.rememberPreference
import labs.lucka.refrain.ui.content.settings.compose.LabeledSwitch
import labs.lucka.refrain.ui.content.settings.compose.Section

@Composable
fun PowerSection(mutable: Boolean) {
    var keepScreenOn by rememberPreference(Keys.Power.KeepScreenOn, false)
    var wakeLockEnabled by rememberPreference(Keys.Power.WakeLock, false)
    val currentView = LocalView.current
    Section(stringResource(R.string.power)) {
        LabeledSwitch(
            stringResource(R.string.power_enable_wake_lock),
            wakeLockEnabled,
            descriptions = stringResource(R.string.power_enable_wake_lock_description),
            enabled = mutable
        ) { checked ->
            wakeLockEnabled = checked
        }

        LabeledSwitch(stringResource(R.string.power_enable_keep_screen_on), keepScreenOn) { checked ->
            keepScreenOn = checked
            currentView.keepScreenOn = keepScreenOn
        }
    }
}