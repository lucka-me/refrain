package labs.lucka.refrain.ui.content.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.ui.LocalRefrainModel
import labs.lucka.refrain.ui.content.settings.compose.Constants

@Composable
fun SettingsContents() {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Constants.SectionSpace)
    ) {
        val model = LocalRefrainModel.current
        val locationManager = model.locationManager
        if (locationManager != null) {
            ProvidersSection(locationManager.allProviders, !model.tracing) { provider ->
                locationManager.isProviderEnabled(provider)
            }
            Divider()
        }

        FilterSection(!model.tracing)
        Divider()

        IntervalSection(!model.tracing)
        Divider()

        SplitSection(!model.tracing)
        Divider()

        NotificationSection(!model.tracing)
        Divider()

        PowerSection(!model.tracing)
    }
}