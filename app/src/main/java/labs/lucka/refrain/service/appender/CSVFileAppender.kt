package labs.lucka.refrain.service.appender

import android.content.ContentResolver
import android.location.Location
import androidx.documentfile.provider.DocumentFile
import java.io.PrintWriter
import java.time.ZonedDateTime

class CSVFileAppender : FileAppender {
    override fun append(location: Location) {
        writer.println(
            "${location.time},${location.provider}," +
                    "${location.longitude},${location.latitude},${location.altitude}," +
                    "${location.bearing},${location.speed},${location.accuracy}"
        )
    }

    override fun finish() {
        writer.flush()
        writer.close()
    }

    override fun prepare(contentResolver: ContentResolver, tree: DocumentFile, dateTime: ZonedDateTime) : Boolean {
        val timestamp = dateTime.toInstant().toEpochMilli()
        val document = tree.createFile("text/csv", "$timestamp") ?: return false
        val outputStream = contentResolver.openOutputStream(document.uri) ?: return false
        writer = PrintWriter(outputStream)
        writer.println("timestamp,provider,longitude,latitude,altitude,bearing,speed,accuracy")
        writer.flush()
        return true
    }

    private lateinit var writer: PrintWriter
}
