package labs.lucka.refrain.ui.card

import android.location.LocationManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
fun ProviderCard(mutable: Boolean) {
    var provider by rememberPreference(Keys.provider, LocationManager.GPS_PROVIDER)
    data class ProviderData(val name: String, val icon: ImageVector)
    val providers = mapOf(
        LocationManager.GPS_PROVIDER to ProviderData("Satellite", Icons.Filled.SatelliteAlt),
        LocationManager.FUSED_PROVIDER to ProviderData("Fused", Icons.Filled.Lightbulb),
        LocationManager.NETWORK_PROVIDER to ProviderData("Network", Icons.Filled.CellTower),
        LocationManager.PASSIVE_PROVIDER to ProviderData("Passive", Icons.Filled.DarkMode)
    )
    Card {
        Column(
            modifier = Modifier
                .selectableGroup()
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                if (mutable) "Provider" else "Provider: ${providers[provider]?.name ?: "Undefined"}",
                if (mutable) Icons.Filled.WifiTethering else providers[provider]?.icon ?: Icons.Filled.QuestionMark,
                "Location Provider",
                MaterialTheme.typography.titleLarge
            )
            if (mutable) {
                for (pair in providers) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = provider == pair.key,
                                role = Role.RadioButton,
                                onClick = { provider = pair.key }
                            ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = provider == pair.key, onClick = null)
                        Label(
                            text = pair.value.name,
                            imageVector = pair.value.icon,
                            imageDescription = pair.value.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
