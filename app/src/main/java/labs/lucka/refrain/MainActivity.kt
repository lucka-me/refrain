package labs.lucka.refrain

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.common.preferencesDataStore
import labs.lucka.refrain.ui.RefrainApp
import labs.lucka.refrain.ui.RefrainModel

class MainActivity : ComponentActivity() {

    private lateinit var model: RefrainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this).get()
        model.onCreate(this)
        setContent {
            RefrainApp(model)
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