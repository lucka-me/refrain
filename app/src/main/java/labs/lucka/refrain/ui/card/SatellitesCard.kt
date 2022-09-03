package labs.lucka.refrain.ui.card

import android.location.GnssStatus
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.Label

@Composable
fun SatellitesCard(gnssStatus: GnssStatus?) {
    if (gnssStatus == null) {
        Card {
            Column(
                modifier = Modifier
                    .padding(all = Constants.CardPadding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
            ) {
                Label(
                    text = "Fixing...",
                    imageVector = Icons.Filled.SignalCellular0Bar,
                    imageDescription = "Fixing",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        ExpandableCard(
            title = "Satellites",
            imageVector = Icons.Filled.SignalCellular4Bar,
            imageDescription = "Satellites",
            alwaysDisplayedContent = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(text = "Total", imageVector = Icons.Filled.SatelliteAlt, imageDescription = "Count")
                    Text(text = gnssStatus.satelliteCount.toString())
                }
            }
        ) {
            val constellationCount = mutableMapOf<Int, UInt>()
            for (index in 0 until gnssStatus.satelliteCount) {
                constellationCount.merge(gnssStatus.getConstellationType(index), 1U, UInt::plus)
            }
            constellationCount.forEach { (constellation, count) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val name = when (constellation) {
                        GnssStatus.CONSTELLATION_GPS -> "GPS"
                        GnssStatus.CONSTELLATION_SBAS -> "SBAS"
                        GnssStatus.CONSTELLATION_GLONASS -> "GLONASS"
                        GnssStatus.CONSTELLATION_QZSS -> "QZSS"
                        GnssStatus.CONSTELLATION_BEIDOU -> "Beidou"
                        GnssStatus.CONSTELLATION_GALILEO -> "Galileo"
                        GnssStatus.CONSTELLATION_IRNSS -> "IRNSS"
                        else -> "Unknown"
                    }
                    Label(
                        text = name,
                        imageVector = Icons.Filled.SatelliteAlt,
                        imageDescription = "Count for $name satellites"
                    )
                    Text(text = count.toString())
                }
            }
        }
    }
}