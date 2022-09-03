package labs.lucka.refrain.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.card.BatteryOptimizationCard
import labs.lucka.refrain.ui.card.LocationPermissionCard
import labs.lucka.refrain.ui.card.OutputPathCard
import labs.lucka.refrain.ui.compose.rememberPreference
import labs.lucka.refrain.ui.theme.RefrainTheme

@Composable
fun RefrainApp(model: RefrainModel) {
    RefrainTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!model.locationPermissionsGranted) {
                    val context = LocalContext.current
                    LocationPermissionCard(onResult = { model.handlePermissionResults(context, it) })
                    if (model.ignoringBatteryOptimization == false) {
                        BatteryOptimizationCard()
                    }
                }
                val outputPath by rememberPreference(Keys.outputPath, "")
                if (outputPath.isEmpty()) {
                    OutputPathCard(true)
                }
                if (model.locationPermissionsGranted&& outputPath.isNotEmpty()) {
                    MainContents(model = model)
                }
            }
        }
    }
}