package labs.lucka.refrain.ui.card

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.compose.Label

@Composable
fun BatteryOptimizationCard() {
    OutlinedCard {
        Column(
            modifier = Modifier
                .padding(all = Constants.CardPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Constants.ContentSpace)
        ) {
            Label(
                stringResource(R.string.battery_optimization),
                Icons.Filled.BatterySaver,
                style = MaterialTheme.typography.titleLarge
            )
            Text(stringResource(R.string.battery_optimization_description))
            val context = LocalContext.current
            TextButton(
                onClick = {
                    @SuppressLint("BatteryLife")
                    val intent = Intent().apply {
                        action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                }
            ) {
                Text(stringResource(R.string.battery_optimization_configure))
            }
        }
    }
}