package labs.lucka.refrain.ui.content.settings

import android.location.LocationManager
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.content.compose.rememberPreference
import labs.lucka.refrain.ui.content.settings.compose.LabeledRadioButton
import labs.lucka.refrain.ui.content.settings.compose.Section

@Composable
fun ProvidersSection(providers: List<String>, mutable: Boolean, isProviderEnabled: (provider: String) -> Boolean) {
    var selectedProvider by rememberPreference(Keys.Provider, LocationManager.GPS_PROVIDER)
    fun nameIdOf(provider: String): Int {
        return when (provider) {
            LocationManager.GPS_PROVIDER -> R.string.settings_providers_gps
            LocationManager.FUSED_PROVIDER -> R.string.settings_providers_fused
            LocationManager.NETWORK_PROVIDER -> R.string.settings_providers_network
            LocationManager.PASSIVE_PROVIDER -> R.string.settings_providers_passive
            else -> R.string.settings_providers_undefined
        }
    }
    Section(stringResource(R.string.settings_providers), modifier = Modifier.selectableGroup()) {
        for (provider in providers) {
            var text = stringResource(nameIdOf(provider))
            if (!isProviderEnabled(provider)) {
                text += stringResource(R.string.settings_providers_disabled_suffix)
            }
            LabeledRadioButton(text, selectedProvider == provider, enabled = mutable) {
                selectedProvider = provider
            }
        }
    }
}
