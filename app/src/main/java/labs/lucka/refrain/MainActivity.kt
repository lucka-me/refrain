package labs.lucka.refrain

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.common.preferencesDataStore
import labs.lucka.refrain.ui.LocalRefrainModel
import labs.lucka.refrain.ui.RefrainApp
import labs.lucka.refrain.ui.RefrainModel

class MainActivity : ComponentActivity() {
    private val model: RefrainModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.onCreate(this)
        setContent {
            CompositionLocalProvider(
                LocalRefrainModel provides model
            ) {
                RefrainApp()
            }
        }
    }

    override fun onPause() {
        model.onPause(this)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        model.onResume(this)
        lifecycleScope.launch {
            if (preferencesDataStore.data.map { it[Keys.Power.KeepScreenOn] == true }.first()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}