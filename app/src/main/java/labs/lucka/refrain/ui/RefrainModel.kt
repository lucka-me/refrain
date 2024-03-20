package labs.lucka.refrain.ui

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.core.location.GnssStatusCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.lucka.refrain.service.TraceService

class RefrainModel : ViewModel() {

    var count: UInt by mutableStateOf(0U)
        private set

    var totalDistance: Float by mutableFloatStateOf(0.0F)
        private set

    var ignoringBatteryOptimization: Boolean? by mutableStateOf(null)
        private set
    var latestGnssStatus: GnssStatusCompat? by mutableStateOf(null)
        private set
    var latestLocation: Location? by mutableStateOf(null)
        private set
    var serviceConnected: Boolean by mutableStateOf(false)
        private set
    var tracing: Boolean by mutableStateOf(false)
        private set

    var locationManager: LocationManager? = null
        private set

    fun onCreate(context: Context) {
        locationManager = context.getSystemService()
    }

    fun onPause(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }

        if(!tracing) {
            context.stopService(serviceIntent)
        }
    }

    fun onResume(context: Context) {
        val serviceIntent = Intent(context, TraceService::class.java)
        this.serviceIntent = serviceIntent
        context.startForegroundService(serviceIntent)
        serviceBound = context.bindService(
            serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE
        )

        ignoringBatteryOptimization =
            context.getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun toggle() {
        val binder = serviceBinder ?: return
        if (!serviceConnected) return
        if (tracing) {
            binder.service.stop()
        } else {
            viewModelScope.launch { binder.service.start() }
        }
    }

    private var serviceBinder: TraceService.TraceBinder? = null
    private var serviceBound = false
    private var serviceIntent: Intent? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val typedBinder = service as? TraceService.TraceBinder ?: return
            serviceBinder = typedBinder
            typedBinder.service.attach(traceListener)
            serviceConnected = true
            tracing = typedBinder.service.tracing
            count = typedBinder.service.count
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBinder?.service?.detach(traceListener)
            serviceBinder = null
            serviceConnected = false
        }
    }

    private val traceListener = object : TraceService.TraceListener {
        override fun onGnssStatusUpdated(status: GnssStatusCompat) {
            latestGnssStatus = status
        }

        override fun onLocationUpdated(count: UInt, location: Location) {
            this@RefrainModel.count = count
            val currentLatestLocation = latestLocation
            if (currentLatestLocation != null) {
                totalDistance += currentLatestLocation.distanceTo(location)
            }
            latestLocation = location
        }

        override fun onStart(succeed: Boolean) {
            tracing = succeed
        }

        override fun onStop() {
            tracing = false
            count = 0U
            totalDistance = 0.0F
            latestLocation = null
        }
    }
}

val LocalRefrainModel = compositionLocalOf<RefrainModel> { error("The view model is missing") }