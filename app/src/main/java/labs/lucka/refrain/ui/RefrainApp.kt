package labs.lucka.refrain.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import labs.lucka.refrain.R
import labs.lucka.refrain.ui.content.main.MainContents
import labs.lucka.refrain.ui.theme.RefrainTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun RefrainApp(model: RefrainModel) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    RefrainTheme {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { MediumTopAppBar({ Text(stringResource(R.string.app_name)) }, scrollBehavior = scrollBehavior) }
        ) { contentPadding ->
            MainContents(model, contentPadding)
        }
    }
}