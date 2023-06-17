package labs.lucka.refrain.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.content.main.MainContents
import labs.lucka.refrain.ui.content.settings.SettingsContents
import labs.lucka.refrain.ui.theme.RefrainTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RefrainApp() {
    var presentingSettingsSheet by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    RefrainTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    { Text(stringResource(R.string.app_name)) },
                    actions = {
                        IconButton({ presentingSettingsSheet = true }) {
                            Icon(Icons.Outlined.Settings, "Settings")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { contentPadding ->
            MainContents(contentPadding)
        }

        if (presentingSettingsSheet) {
            ModalBottomSheet({ presentingSettingsSheet = false }) {
                SettingsContents()
            }
        }
    }
}