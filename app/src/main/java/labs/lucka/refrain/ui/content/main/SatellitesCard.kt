package labs.lucka.refrain.ui.content.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.SignalCellular0Bar
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.location.GnssStatusCompat
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.content.compose.ExpandableCard
import labs.lucka.refrain.ui.content.compose.Label
import labs.lucka.refrain.ui.content.main.compose.Constants

@Composable
fun SatellitesCard(gnssStatus: GnssStatusCompat?) {
    if (gnssStatus == null) {
        OutlinedCard {
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
        return
    }

    fun nameIdOf(constellation: Int): Int {
        return when (constellation) {
            GnssStatusCompat.CONSTELLATION_GPS -> R.string.satellites_gps
            GnssStatusCompat.CONSTELLATION_SBAS -> R.string.satellites_sbas
            GnssStatusCompat.CONSTELLATION_GLONASS -> R.string.satellites_glonass
            GnssStatusCompat.CONSTELLATION_QZSS -> R.string.satellites_qzss
            GnssStatusCompat.CONSTELLATION_BEIDOU -> R.string.satellites_beidou
            GnssStatusCompat.CONSTELLATION_GALILEO -> R.string.satellites_galileo
            GnssStatusCompat.CONSTELLATION_IRNSS -> R.string.satellites_irnss
            else -> R.string.satellites_unknown
        }
    }

    val constellationCount = mutableMapOf<Int, Pair<Int, Int>>()
    var usedInFixCount = 0
    for (index in 0 until gnssStatus.satelliteCount) {
        val usedInFix = gnssStatus.usedInFix(index)
        constellationCount.merge(
            gnssStatus.getConstellationType(index),
            Pair(1, if (usedInFix) 1 else 0)
        ) { a, b -> Pair(a.first + b.first, a.second + b.second) }
        if (usedInFix) {
            usedInFixCount += 1
        }
    }

    ExpandableCard(
        title = stringResource(R.string.satellites),
        imageVector = Icons.Filled.SignalCellular4Bar,
        alwaysDisplayedContent = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Label(stringResource(R.string.satellites_total), Icons.Filled.SatelliteAlt)
                Text("$usedInFixCount / ${gnssStatus.satelliteCount}")
            }
        }
    ) {
        constellationCount.forEach { (constellation, count) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val nameId = nameIdOf(constellation)
                Label(
                    stringResource(nameId),
                    Icons.Filled.SatelliteAlt,
                    imageDescription = stringResource(R.string.satellites_count_description, stringResource(nameId))
                )
                Text("${count.second} / ${count.first}")
            }
        }
    }
}