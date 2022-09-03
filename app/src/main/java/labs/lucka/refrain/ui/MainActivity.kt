package labs.lucka.refrain.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get

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
    }
}