package labs.lucka.refrain.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import labs.lucka.refrain.ui.card.Constants

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ExpandableCard(
    title: String,
    imageVector: ImageVector,
    imageDescription: String,
    alwaysDisplayedContent: @Composable (() -> Unit)? = null,
    collapsableContent: @Composable () -> Unit
) {
    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Label(title, imageVector, imageDescription, style = MaterialTheme.typography.titleLarge)
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.size(
                        with(LocalDensity.current) { MaterialTheme.typography.titleLarge.fontSize.toDp() }
                    )
                )
            }

            if (alwaysDisplayedContent != null) {
                alwaysDisplayedContent()
            }

            if (expanded) {
                collapsableContent()
            }
        }
    }
}