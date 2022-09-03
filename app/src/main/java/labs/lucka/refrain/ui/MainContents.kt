package labs.lucka.refrain.ui

import androidx.compose.runtime.Composable
import labs.lucka.refrain.ui.card.*

@Composable
fun MainContents(model: RefrainModel) {
    TracingStatusCard(tracing = model.tracing) {
        model.toggle()
    }
    if (model.tracing) {
        LatestLocationCard(count = model.count, location = model.latestLocation)
        SatellitesCard(model.latestGnssStatus)
    }
    ProviderCard(!model.tracing)
    OutputFormatCard(!model.tracing)
    FilterCard(!model.tracing)
    IntervalsCard(!model.tracing)
    OutputPathCard(!model.tracing)

    if (model.ignoringBatteryOptimization == false) {
        BatteryOptimizationCard()
    }
}