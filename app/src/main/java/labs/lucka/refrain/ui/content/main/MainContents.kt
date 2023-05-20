package labs.lucka.refrain.ui.content.main

import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.ui.RefrainModel
import labs.lucka.refrain.ui.card.*
import labs.lucka.refrain.ui.compose.Label
import labs.lucka.refrain.ui.compose.rememberPreference

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun MainContents(model: RefrainModel, contentPadding: PaddingValues) {
    val context = LocalContext.current
    val locationManager = context.getSystemService<LocationManager>()
    if (locationManager == null) {
        Label(
            "The Location Service is unavailable in your device.",
            Icons.Filled.Dangerous,
            style = MaterialTheme.typography.titleLarge
        )
        return
    }
    val outputPath by rememberPreference(Keys.OutputPath, "")
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) { granted ->
        if (granted) model.onLocationPermissionGranted(context)
    }
    LazyColumn(
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (locationPermissionState.status != PermissionStatus.Granted) {
            item {
                LocationPermissionCard {
                    locationPermissionState.launchPermissionRequest()
                }
            }
        }

        if (outputPath.isEmpty()) {
            item { OutputPathCard(true) }
        }
        if (locationPermissionState.status == PermissionStatus.Granted && outputPath.isNotEmpty()) {
            item { TracingStatusCard(model.tracing) { model.toggle() } }
            if (model.tracing) {
                item { LatestLocationCard(model.count, model.latestLocation) }
                item { SatellitesCard(model.latestGnssStatus) }
            }
            item { ProviderCard(locationManager, !model.tracing) }
            item { OutputFormatCard(!model.tracing) }
            item { FilterCard(!model.tracing) }
            item { IntervalsCard(!model.tracing) }
            item { SplitCard(!model.tracing) }
            item { OutputPathCard(!model.tracing) }
        }

        if (model.ignoringBatteryOptimization == false) {
            item { BatteryOptimizationCard() }
        }

        item { AboutRow() }
    }
}