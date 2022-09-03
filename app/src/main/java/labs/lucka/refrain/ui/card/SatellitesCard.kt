package labs.lucka.refrain.ui.card

import android.location.GnssStatus
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
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
                    text = stringResource(R.string.satellites_fixing),
                    imageVector = Icons.Filled.SignalCellular0Bar,
                    imageDescription = stringResource(R.string.satellites_fixing_alt),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        ExpandableCard(
            title = stringResource(R.string.satellites),
            imageVector = Icons.Filled.SignalCellular4Bar,
            alwaysDisplayedContent = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(stringResource(R.string.satellites_total), Icons.Filled.SatelliteAlt)
                    Text(gnssStatus.satelliteCount.toString())
                }
            }
        ) {
            val constellationCount = mutableMapOf<Int, UInt>()
            for (index in 0 until gnssStatus.satelliteCount) {
                constellationCount.merge(gnssStatus.getConstellationType(index), 1U, UInt::plus)
            }
            constellationCount.forEach { (constellation, count) ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val nameId = when (constellation) {
                        GnssStatus.CONSTELLATION_GPS -> R.string.satellites_gps
                        GnssStatus.CONSTELLATION_SBAS -> R.string.satellites_sbas
                        GnssStatus.CONSTELLATION_GLONASS -> R.string.satellites_glonass
                        GnssStatus.CONSTELLATION_QZSS -> R.string.satellites_qzss
                        GnssStatus.CONSTELLATION_BEIDOU -> R.string.satellites_beidou
                        GnssStatus.CONSTELLATION_GALILEO -> R.string.satellites_galileo
                        GnssStatus.CONSTELLATION_IRNSS -> R.string.satellites_irnss
                        else -> R.string.satellites_unknown
                    }
                    Label(
                        stringResource(nameId),
                        Icons.Filled.SatelliteAlt,
                        imageDescription = stringResource(R.string.satellites_count_description, stringResource(nameId))
                    )
                    Text(text = count.toString())
                }
            }
        }
    }
}