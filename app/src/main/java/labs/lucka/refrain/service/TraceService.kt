
package labs.lucka.refrain.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.core.location.GnssStatusCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.common.preferencesDataStore
import labs.lucka.refrain.service.appender.FileAppender
import labs.lucka.refrain.MainActivity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TraceService : Service() {

    inner class TraceBinder : Binder() {
        val service: TraceService
            get() = this@TraceService
    }

    interface TraceListener {
        fun onGnssStatusUpdated(status: GnssStatusCompat)
        fun onLocationUpdated(count: UInt, location: Location)
        fun onStart(succeed: Boolean)
        fun onStop()
    }

    companion object {
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 1
        private const val FOREGROUND_SERVICE_NOTIFICATION_ACTION_TOGGLE = "TraceServiceToggle"
        private const val GNSS_STATUS_STOPPED_NOTIFICATION_ID = 11
        private const val NOTIFICATION_CHANNEL_ID = "TraceServiceNotification"
        private const val WAKE_LOCK_TAG = "Refrain::TraceService"
    }

    var count: UInt = 0U
        private set
    var tracing = false
        private set

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        job.cancel()
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            wakeLock = null
        }
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val currentNotificationManager = applicationContext.getSystemService<NotificationManager>()
        if (currentNotificationManager != null) {
            currentNotificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID, getText(R.string.app_name), NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.service_foreground_channel_description)
                    setSound(null, null)
                }
            )
            notificationManager = currentNotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FOREGROUND_SERVICE_NOTIFICATION_ID,
                buildServiceNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, buildServiceNotification())
        }

        when (intent.action) {
            FOREGROUND_SERVICE_NOTIFICATION_ACTION_TOGGLE -> if (tracing) stop() else supervisorScope.launch { start() }
        }

        return START_STICKY
    }

    fun attach(listener: TraceListener) {
        if (!listeners.contains(listener)) listeners.add(listener)
    }

    fun detach(listener: TraceListener) {
        listeners.remove(listener)
    }

    fun stop() {
        tracing = false
        val locationManager = applicationContext.getSystemService<LocationManager>()
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener)
            LocationManagerCompat.unregisterGnssStatusCallback(locationManager, gnssStatusCallback)
        }
        notifyStop()
        for (appender in appenders) {
            appender.finish()
        }
        notificationManager?.notify(FOREGROUND_SERVICE_NOTIFICATION_ID, buildServiceNotification())
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            wakeLock = null
        }
    }

    @SuppressLint("WakelockTimeout")
    suspend fun start() {
        // Appenders
        val outputPath = preferencesDataStore.data.map { it[Keys.OutputPath] ?: "" }.first()
        val outputTree = DocumentFile.fromTreeUri(this, Uri.parse(outputPath))
        if (outputTree == null) {
            mainExecutor.execute { notifyStart(false) }
            return
        }
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val displayName = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
        appenders.clear()
        if (preferencesDataStore.data.map { it[Keys.OutputFormat.CSV] != false }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.CSV).build()
            if (!appender.prepare(contentResolver, outputTree, displayName)) {
                mainExecutor.execute { notifyStart(false) }
                return
            }
            appenders.add(appender)
        }
        if (preferencesDataStore.data.map { it[Keys.OutputFormat.GPX] == true }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.GPX).build()
            if (!appender.prepare(contentResolver, outputTree, displayName)) {
                mainExecutor.execute { notifyStart(false) }
                return
            }
            appenders.add(appender)
        }
        if (preferencesDataStore.data.map { it[Keys.OutputFormat.KML] == true }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.KML).build()
            if (!appender.prepare(contentResolver, outputTree, displayName)) {
                mainExecutor.execute { notifyStart(false) }
                return
            }
            appenders.add(appender)
        }

        // Location Manager
        val locationManager = applicationContext.getSystemService<LocationManager>()
        if (locationManager == null) {
            mainExecutor.execute { notifyStart(false) }
            return
        }

        accuracyFilter = preferencesDataStore.data.map { it[Keys.Filter.Accuracy] ?: 0F }.first()
        ignoreDuplicated = preferencesDataStore.data.map { it[Keys.Filter.IgnoreDuplicated] == true }.first()

        val timeInterval = preferencesDataStore.data.map { it[Keys.Interval.Time] ?: 0 }.first()
        val distanceInterval = preferencesDataStore.data.map { it[Keys.Interval.Distance] ?: 0F }.first()

        splitTimeInterval = preferencesDataStore.data.map { it[Keys.Split.Time] ?: 0 }.first() * 1000
        splitDistanceInterval = preferencesDataStore.data.map { it[Keys.Split.Distance] ?: 0F }.first()

        notifyWhenGnssStops = preferencesDataStore.data.map {
            it[Keys.Notification.NotifyWhenGnssStops] == true
        }.first()

        val newLocationRequest = LocationRequestCompat.Builder(timeInterval * 1000)
            .setQuality(LocationRequestCompat.QUALITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(distanceInterval)
            .build()

        provider = preferencesDataStore.data.map { it[Keys.Provider] ?: LocationManager.GPS_PROVIDER }.first()

        mainExecutor.execute {
            count = 0U
            lastLocation = null
        }

        try {
            LocationManagerCompat.requestLocationUpdates(
                locationManager, provider, newLocationRequest, mainExecutor, locationListener
            )
            LocationManagerCompat.registerGnssStatusCallback(
                locationManager, mainExecutor, gnssStatusCallback
            )
        } catch (e: SecurityException) {
            mainExecutor.execute { notifyStart(false) }
            return
        }

        locationRequest = newLocationRequest

        mainExecutor.execute {
            tracing = true
            notificationManager?.notify(FOREGROUND_SERVICE_NOTIFICATION_ID, buildServiceNotification())
            notifyStart(true)
        }

        if (preferencesDataStore.data.map { it[Keys.Power.WakeLock] == true }.first()) {
            wakeLock = getSystemService<PowerManager>()
                ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG)?.apply {
                    acquire()
                }
        }
    }

    private var accuracyFilter = 0F
    private var appenders = mutableListOf<FileAppender>()
    private val binder = TraceBinder()
    private val job = SupervisorJob()
    private var lastLocation: Location? = null
    private var locationRequest: LocationRequestCompat? = null
    private var listeners = mutableListOf<TraceListener>()
    private var notificationManager: NotificationManager? = null
    private var notifyWhenGnssStops = false
    private var provider = ""
    private var splitTimeInterval: Long = 0
    private var splitDistanceInterval = 0F
    private var ignoreDuplicated = false
    private val supervisorScope = CoroutineScope(Dispatchers.Main + job)
    private var wakeLock: PowerManager.WakeLock? = null

    private val gnssStatusCallback = object: GnssStatusCompat.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatusCompat) {
            super.onSatelliteStatusChanged(status)
            for (listener in listeners) {
                listener.onGnssStatusUpdated(status)
            }
        }

        override fun onStarted() {
            super.onStarted()
            notificationManager?.cancel(GNSS_STATUS_STOPPED_NOTIFICATION_ID)
        }

        override fun onStopped() {
            super.onStopped()
            // The location update is actually suspended and the system begins to supply the listener with the same
            // location, notify the user to unlock the device to activate GNSS system
            if (!notifyWhenGnssStops) return
            val notification = Notification.Builder(this@TraceService, NOTIFICATION_CHANNEL_ID)
                .setCategory(Notification.CATEGORY_ERROR)
                .setContentIntent(mainActivityPendingIntent)
                .setContentText(getString(R.string.service_notification_gnss_stops_content))
                .setContentTitle(getString(R.string.service_notification_gnss_stops_title))
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build()
            notificationManager?.notify(GNSS_STATUS_STOPPED_NOTIFICATION_ID, notification)

            val locationManager = applicationContext.getSystemService<LocationManager>()
            val currentLocationRequest = locationRequest
            if (locationManager != null && currentLocationRequest != null) {
                try {
                    LocationManagerCompat.getCurrentLocation(
                        locationManager, provider, null, mainExecutor,
                    ) { location ->
                        locationListener.onLocationChanged(location)
                    }
                } catch (_: SecurityException) {

                }
            }
        }
    }

    private val locationListener = LocationListenerCompat { location ->
        if (accuracyFilter > 0 && location.accuracy > accuracyFilter) return@LocationListenerCompat

        val lastLocation = lastLocation
        if (lastLocation != null) {
            if (ignoreDuplicated
                && lastLocation.longitude == location.longitude
                && lastLocation.latitude == location.latitude
                && lastLocation.altitude == location.altitude
                && lastLocation.accuracy == location.accuracy
            ) {
                return@LocationListenerCompat
            }
            if (
                (splitTimeInterval > 0 && location.time - lastLocation.time > splitTimeInterval) ||
                (splitDistanceInterval > 0 && lastLocation.distanceTo(location) > splitDistanceInterval)
            ) {
                for (appender in appenders) {
                    appender.split()
                }
            }
        }

        count++
        for (appender in appenders) {
            appender.append(location)
        }
        for (listener in listeners) {
            listener.onLocationUpdated(count, location)
        }

        this.lastLocation = location

        notificationManager?.notify(FOREGROUND_SERVICE_NOTIFICATION_ID, buildServiceNotification())
    }

    private val mainActivityPendingIntent: PendingIntent
        get() {
            return TaskStackBuilder.create(this)
                .addNextIntent(Intent(this, MainActivity::class.java))
                .getPendingIntent(
                    0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        }

    private fun buildServiceNotification(): Notification {
        // Toggle action
        val toggleAction = Notification.Action.Builder(
            Icon.createWithResource(
                this, if (tracing) R.drawable.ic_baseline_stop_24 else R.drawable.ic_baseline_play_arrow_24
            ),
            getString(if (tracing) R.string.stop else R.string.start),
            PendingIntent.getService(
                this,
                0,
                Intent(
                    this,
                    TraceService::class.java).apply { action = FOREGROUND_SERVICE_NOTIFICATION_ACTION_TOGGLE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
            .build()

        val builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .addAction(toggleAction)
            .setCategory(Notification.CATEGORY_STATUS)
            .setContentIntent(mainActivityPendingIntent)
            .setContentText(
                getString(
                    if (tracing) {
                        R.string.service_notification_service_tracing
                    } else {
                        R.string.service_notification_service_standby
                    }
                )
            )
            .setContentTitle(getText(R.string.app_name))
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setVisibility(Notification.VISIBILITY_PUBLIC)

        if (tracing) {
            builder.setSubText("# $count")
        }

        return builder.build()
    }

    private fun notifyStart(succeed: Boolean) {
        for (listener in listeners) {
            listener.onStart(succeed)
        }
    }

    private fun notifyStop() {
        for (listener in listeners) {
            listener.onStop()
        }
    }
}