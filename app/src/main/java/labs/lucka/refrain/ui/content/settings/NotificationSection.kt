package labs.lucka.refrain.ui.content.settings

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.content.compose.rememberPreference
import labs.lucka.refrain.ui.content.settings.compose.ClickableItem
import labs.lucka.refrain.ui.content.settings.compose.LabeledSwitch
import labs.lucka.refrain.ui.content.settings.compose.Section

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun NotificationSection(mutable: Boolean) {
    var notifyWhenGnssStops by rememberPreference(Keys.Notification.NotifyWhenGnssStops, false)
    Section(stringResource(R.string.settings_notification)) {
        LabeledSwitch(
            stringResource(R.string.settings_notification_send_when_gnss_stops),
            notifyWhenGnssStops,
            descriptions = stringResource(R.string.settings_notification_send_when_gnss_stops_description),
            enabled = mutable
        ) { checked ->
            notifyWhenGnssStops = checked
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionState = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

            if (notificationPermissionState.status != PermissionStatus.Granted) {
                ClickableItem(
                    stringResource(R.string.settings_notification_request_permission),
                    description = stringResource(R.string.settings_notification_request_permission_description),
                ) {
                    notificationPermissionState.launchPermissionRequest()
                }
            }
        }
    }
}