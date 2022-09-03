package labs.lucka.refrain.ui.card

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.compose.ExpandableCard
import labs.lucka.refrain.ui.compose.Label

@Composable
fun LatestLocationCard(count: UInt, location: Location?) {
    if (location == null) {
        Card {
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