package labs.lucka.refrain.ui.content.main

import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.content.compose.ExpandableCard
import labs.lucka.refrain.ui.content.compose.Label
import labs.lucka.refrain.ui.content.main.compose.Constants

@Composable
fun LatestLocationCard(count: UInt, location: Location?) {
    if (location == null) {
        OutlinedCard {
            Column(
                modifier = Modifier
                    .padding(all = Constants.CardPadding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
            ) {
                Label(
                    text = stringResource(R.string.last_location_locating),
                    imageVector = Icons.Filled.LocationSearching,
                    imageDescription = stringResource(R.string.last_location_locating_alt),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        ExpandableCard(
            title = "# $count",
            imageVector = Icons.Filled.MyLocation,
            imageDescription = stringResource(R.string.last_location),
            alwaysDisplayedContent = {
                Label(Icons.Filled.Place, stringResource(R.string.last_location_coordinate)) {
                    Text(
                        text = "${location.longitude}, ${location.latitude}",
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        ) {
            if (location.hasAccuracy()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(stringResource(R.string.accuracy), Icons.Filled.Adjust)
                    Text(text = "${location.accuracy} m")
                }
            }
            if (location.hasSpeed()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(stringResource(R.string.last_location_speed), Icons.Filled.Speed)
                    Text(text = "${location.speed} m/s")
                }
            }
        }
    }
}