
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
import java.time.format.DateTimeFormatter

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
        private const val NOTIFICATION_ACTION_TOGGLE = "TraceServiceToggle"
        private const val NOTIFICATION_CHANNEL_ID = "TraceServiceNotification"
        private const val NOTIFICATION_ID = 1
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
        val currentNotificationManager = applicationContext.getSystemService<NotificationManager>()
        if (currentNotificationManager != null) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, getText(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.service_foreground_channel_description)
            notificationChannel.setSound(null, null)
            currentNotificationManager.createNotificationChannel(notificationChannel)
            notificationManager = currentNotificationManager
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID, buildNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, buildNotification())
        }

        when (intent.action) {
            NOTIFICATION_ACTION_TOGGLE -> if (tracing) stop() else supervisorScope.launch { start() }
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
        applicationContext.getSystemService<LocationManager>()
            ?.removeUpdates(locationListener)
        notifyStop()
        for (appender in appenders) {
            appender.finish()
        }
        notificationManager?.notify(NOTIFICATION_ID, buildNotification())
    }

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
        val timeInterval = preferencesDataStore.data.map { it[Keys.Interval.Time] ?: 0 }.first()
        val distanceInterval = preferencesDataStore.data.map { it[Keys.Interval.Distance] ?: 0F }.first()

        splitTimeInterval = preferencesDataStore.data.map { it[Keys.Split.Time] ?: 0 }.first() * 1000
        splitDistanceInterval = preferencesDataStore.data.map { it[Keys.Split.Distance] ?: 0F }.first()

        val locationRequest = LocationRequestCompat.Builder(timeInterval * 1000)
            .setQuality(LocationRequestCompat.QUALITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(distanceInterval)
            .build()

        val provider = preferencesDataStore.data.map { it[Keys.Provider] ?: LocationManager.GPS_PROVIDER }.first()

        try {
            LocationManagerCompat.requestLocationUpdates(
                locationManager, provider, locationRequest, mainExecutor, locationListener
            )
        } catch (e: SecurityException) {
            mainExecutor.execute { notifyStart(false) }
            return
        }
        mainExecutor.execute {
            count = 0U
            tracing = true
            lastLocation = null
            notificationManager?.notify(NOTIFICATION_ID, buildNotification())
            notifyStart(true)
        }
    }

    private var accuracyFilter = 0F
    private var appenders = mutableListOf<FileAppender>()
    private val binder = TraceBinder()
    private val job = SupervisorJob()
    private var lastLocation: Location? = null
    private var listeners = mutableListOf<TraceListener>()
    private var notificationManager: NotificationManager? = null
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

        notificationManager?.notify(NOTIFICATION_ID, buildNotification())
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
                Intent(this, TraceService::class.java).apply { action = NOTIFICATION_ACTION_TOGGLE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
            .build()

        val builder = Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_STATUS)
            .setContentTitle(getText(R.string.app_name))
            .setContentText(
                getString(if (tracing) R.string.service_notification_tracing else R.string.service_notification_standby)
            )
            .setContentIntent(contentPendingIntent)
            .addAction(toggleAction)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)

        if (tracing) {
            builder.setSubText("# $count")
        }

        return builder.build()
    }
}