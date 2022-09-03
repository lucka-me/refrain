package labs.lucka.refrain.ui.card

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
                    text = "Locating...",
                    imageVector = Icons.Filled.LocationSearching,
                    imageDescription = "Locating",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    } else {
        ExpandableCard(
            title = "# $count",
            imageVector = Icons.Filled.MyLocation,
            imageDescription = "Latest Location",
            alwaysDisplayedContent = {
                Label(imageVector = Icons.Filled.Place, imageDescription = "Coordinate") {
                    Text(
                        text = "${location.longitude}, ${location.latitude}",
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        ) {
            if (location.hasAccuracy()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(text = "Accuracy", imageVector = Icons.Filled.Adjust, imageDescription = "Accuracy")
                    Text(text = "${location.accuracy} m")
                }
            }
            if (location.hasSpeed()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label(text = "Speed", imageVector = Icons.Filled.Speed, imageDescription = "Speed")
                    Text(text = "${location.speed} m/s")
                }
            }
        }
    }
}