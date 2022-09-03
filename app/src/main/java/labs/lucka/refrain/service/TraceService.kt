
package labs.lucka.refrain.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import labs.lucka.refrain.R
import labs.lucka.refrain.common.preferences.Keys
import labs.lucka.refrain.common.preferencesDataStore
import labs.lucka.refrain.service.appender.FileAppender
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
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationManager = applicationContext.getSystemService<NotificationManager>()
        if (notificationManager != null) {
            val notificationChannel = NotificationChannel(
                channelId, getText(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.service_foreground_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                foregroundNotificationId, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(foregroundNotificationId, buildNotification())
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
            applicationContext.getSystemService<NotificationManager>()
                ?.notify(foregroundNotificationId, buildNotification())
            notifyStart(true)
        }
    }

    private var accuracyFilter = 0F
    private var appenders = mutableListOf<FileAppender>()
    private val binder = TraceBinder()
    private var listeners = mutableListOf<TraceListener>()

    private val locationListener = LocationListenerCompat { location ->
        if (accuracyFilter > 0 && location.accuracy > accuracyFilter) return@LocationListenerCompat

        count++
        for (appender in appenders) {
            appender.append(location)
        }
        for (listener in listeners) {
            listener.onLocationUpdated(count, location)
        }
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
        val contentId = if (tracing) R.string.service_notification_tracing else R.string.service_notification_standby
        return Notification.Builder(this, channelId)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(getString(contentId))
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }
}