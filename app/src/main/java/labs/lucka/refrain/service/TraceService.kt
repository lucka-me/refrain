
package labs.lucka.refrain.service

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.getSystemService
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

class TraceService : Service() {

    inner class TraceBinder : Binder() {
        val service: TraceService
            get() = this@TraceService
    }

    interface TraceListener {
        fun onLocationUpdated(count: UInt, location: Location)
        fun onStart(succeed: Boolean)
        fun onStop()
    }

    companion object {
        private const val channelId = "TraceServiceNotification"
        private const val foregroundNotificationId = 1

        private const val toggleAction = "TraceServiceToggle"
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
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationManager = applicationContext.getSystemService<NotificationManager>()
        if (notificationManager != null) {
            val notificationChannel = NotificationChannel(
                channelId, getText(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.service_foreground_channel_description)
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                foregroundNotificationId, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(foregroundNotificationId, buildNotification())
        }

        when (intent.action) {
            toggleAction -> if (tracing) stop() else supervisorScope.launch { start() }
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
        val locationManager = applicationContext.getSystemService(LocationManager::class.java)
        locationManager.removeUpdates(locationListener)
        notifyStop()
        for (appender in appenders) {
            appender.finish()
        }
        applicationContext.getSystemService<NotificationManager>()
            ?.notify(foregroundNotificationId, buildNotification())
    }

    suspend fun start() {
        // Appenders
        val outputPath = preferencesDataStore.data.map { it[Keys.outputPath] ?: "" }.first()
        val outputTree = DocumentFile.fromTreeUri(this, Uri.parse(outputPath))
        if (outputTree == null) {
            mainExecutor.execute { notifyStart(false) }
            return
        }
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault())
        appenders.clear()
        if (preferencesDataStore.data.map { it[Keys.outputFormat.csv] != false }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.CSV).build()
            if (!appender.prepare(contentResolver, outputTree, now)) {
                mainExecutor.execute { notifyStart(false) }
                return
            }
            appenders.add(appender)
        }
        if (preferencesDataStore.data.map { it[Keys.outputFormat.gpx] == true }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.GPX).build()
            if (!appender.prepare(contentResolver, outputTree, now)) {
                mainExecutor.execute { notifyStart(false) }
                return
            }
            appenders.add(appender)
        }
        if (preferencesDataStore.data.map { it[Keys.outputFormat.kml] == true }.first()) {
            val appender = FileAppender.Builder(FileAppender.FileType.KML).build()
            if (!appender.prepare(contentResolver, outputTree, now)) {
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

        accuracyFilter = preferencesDataStore.data.map { it[Keys.filter.accuracy] ?: 0F }.first()
        val timeInterval = preferencesDataStore.data.map { it[Keys.interval.time] ?: 0 }.first()
        val distanceInterval = preferencesDataStore.data.map { it[Keys.interval.distance] ?: 0F }.first()

        splitTimeInterval = preferencesDataStore.data.map { it[Keys.split.time] ?: 0 }.first() * 1000
        splitDistanceInterval = preferencesDataStore.data.map { it[Keys.split.distance] ?: 0F }.first()

        val locationRequest = LocationRequestCompat.Builder(timeInterval * 1000)
            .setQuality(LocationRequestCompat.QUALITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(distanceInterval)
            .build()

        val provider = preferencesDataStore.data.map { it[Keys.provider] ?: LocationManager.GPS_PROVIDER }.first()

        try {
            LocationManagerCompat.requestLocationUpdates(locationManager, provider, locationRequest, mainExecutor, locationListener)
        } catch (e: SecurityException) {
            mainExecutor.execute { notifyStart(false) }
            return
        }
        mainExecutor.execute {
            count = 0U
            tracing = true
            lastLocation = null
            applicationContext.getSystemService<NotificationManager>()
                ?.notify(foregroundNotificationId, buildNotification())
            notifyStart(true)
        }
    }

    private var accuracyFilter = 0F
    private var appenders = mutableListOf<FileAppender>()
    private val binder = TraceBinder()
    private val job = SupervisorJob()
    private var lastLocation: Location? = null
    private var listeners = mutableListOf<TraceListener>()
    private var splitTimeInterval: Long = 0
    private var splitDistanceInterval = 0F
    private val supervisorScope = CoroutineScope(Dispatchers.Main + job)

    private val locationListener = LocationListenerCompat { location ->
        if (accuracyFilter > 0 && location.accuracy > accuracyFilter) return@LocationListenerCompat

        val lastLocation = lastLocation
        if (lastLocation != null) {
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

    private fun buildNotification(): Notification {
        // Open app when tap content
        val contentPendingIntent = TaskStackBuilder.create(this)
            .addNextIntent(Intent(this, MainActivity::class.java))
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Toggle action
        val toggleAction = Notification.Action.Builder(
            Icon.createWithResource(
                this, if (tracing) R.drawable.ic_baseline_stop_24 else R.drawable.ic_baseline_play_arrow_24
            ),
            getString(if (tracing) R.string.stop else R.string.start),
            PendingIntent.getService(
                this,
                0,
                Intent(this, TraceService::class.java).apply { action = toggleAction },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
            .build()

        val contentTextId = if (tracing) R.string.service_notification_tracing else R.string.service_notification_standby
        return Notification.Builder(this, channelId)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getString(contentTextId))
            .setContentIntent(contentPendingIntent)
            .addAction(toggleAction)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }
}