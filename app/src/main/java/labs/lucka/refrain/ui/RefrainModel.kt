package labs.lucka.refrain.ui

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.lucka.refrain.service.TraceService

class RefrainModel : ViewModel() {

    var count: UInt by mutableStateOf(0U)
        private set
    var ignoringBatteryOptimization: Boolean? by mutableStateOf(null)
        private set
    var latestGnssStatus: GnssStatus? by mutableStateOf(null)
        private set
    var latestLocation: Location? by mutableStateOf(null)
        private set
    var locationPermissionsGranted: Boolean by mutableStateOf(false)
        private set
    var serviceConnected: Boolean by mutableStateOf(false)
        private set
    var tracing: Boolean by mutableStateOf(false)
        private set

    fun handlePermissionResults(context: Context, results: Map<String, Boolean>) {
        if (results[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            locationPermissionsGranted = true
            if (!updatingGnssStatus) {
                engageGnssUpdate(context)
            }
        }
    }

    fun onCreate(context: Context) {
        engageService(context)
    }

    fun onPause(context: Context) {
        disengageService(context)
        if (updatingGnssStatus) {
            val locationManager = context.getSystemService<LocationManager>() ?: return
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
            updatingGnssStatus = false
        }
    }

    fun onResume(context: Context) {
        updatePermissionStats(context)
        engageService(context)
        engageGnssUpdate(context)
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
    private var updatingGnssStatus = false

    private val gnssStatusCallback = object: GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            super.onSatelliteStatusChanged(status)
            if (status.satelliteCount > 0) {
                latestGnssStatus = status
            }
        }
    }

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
        override fun onLocationUpdated(count: UInt, location: Location) {
            this@RefrainModel.count = count
            latestLocation = location
        }

        override fun onStart(succeed: Boolean) {
            tracing = succeed
        }

        override fun onStop() {
            tracing = false
            count = 0U
            latestLocation = null
        }
    }

    private fun disengageService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }

        if(!tracing) {
            context.stopService(serviceIntent)
        }
    }

    private fun engageService(context: Context) {
        serviceIntent = Intent(context, TraceService::class.java)
        context.startForegroundService(serviceIntent)
        serviceBound = context.bindService(
            serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE
        )
    }

    private fun engageGnssUpdate(context: Context) {
        if (updatingGnssStatus || !locationPermissionsGranted) return
        val locationManager = context.getSystemService<LocationManager>() ?: return
        try {
            locationManager.registerGnssStatusCallback(context.mainExecutor, gnssStatusCallback)
        } catch (e: SecurityException) {
            return
        }
        updatingGnssStatus = true
    }

    private fun updatePermissionStats(context: Context) {
        locationPermissionsGranted =
            context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
}