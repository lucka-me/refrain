package labs.lucka.refrain.ui.card

import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material.icons.filled.WifiTetheringOff
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun ProviderCard(providers: List<String>, mutable: Boolean, isProviderEnabled: (provider: String) -> Boolean) {
    var selectedProvider by rememberPreference(Keys.Provider, LocationManager.GPS_PROVIDER)
    data class ProviderData(val nameId: Int, val icon: ImageVector)
    val providerDataOf = { provider: String ->
        when (provider) {
            LocationManager.GPS_PROVIDER -> ProviderData(R.string.provider_gps, Icons.Filled.SatelliteAlt)
            LocationManager.FUSED_PROVIDER -> ProviderData(R.string.provider_fused, Icons.Filled.Lightbulb)
            LocationManager.NETWORK_PROVIDER -> ProviderData(R.string.provider_network, Icons.Filled.CellTower)
            LocationManager.PASSIVE_PROVIDER -> ProviderData(R.string.provider_passive, Icons.Filled.DarkMode)
            else -> ProviderData(R.string.provider_undefined, Icons.Filled.QuestionMark)
        }
    }
    Card {
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            if (mutable) {
                Label(
                    stringResource(R.string.provider),
                    Icons.Filled.WifiTethering,
                    stringResource(R.string.provider_alt),
                    MaterialTheme.typography.titleLarge
                )
            } else {
                val data = providerDataOf(selectedProvider)
                Label(
                    stringResource(R.string.provider_by, stringResource(data.nameId, selectedProvider)),
                    data.icon,
                    stringResource(R.string.provider_alt),
                    MaterialTheme.typography.titleLarge
                )
            }

            if (!mutable) {
                return@Card
            }
            for (provider in providers) {
                val data = providerDataOf(provider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedProvider == provider,
                            role = Role.RadioButton,
                            onClick = { selectedProvider = provider }
                        ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selectedProvider == provider, onClick = null)
                    Label(
                        stringResource(data.nameId, selectedProvider),
                        data.icon,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (!isProviderEnabled(provider)) {
                        Spacer(modifier = Modifier.weight(1F))
                        Label(stringResource(id = R.string.provider_disabled), Icons.Filled.WifiTetheringOff)
                    }
                }
            }
        }
    }
}
