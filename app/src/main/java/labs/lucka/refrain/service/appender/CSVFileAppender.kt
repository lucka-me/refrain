package labs.lucka.refrain.service.appender

import android.content.ContentResolver
import android.location.Location
import androidx.documentfile.provider.DocumentFile
import java.io.PrintWriter

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

    override fun prepare(contentResolver: ContentResolver, tree: DocumentFile, displayName: String) : Boolean {
        val document = tree.createFile("text/csv", displayName) ?: return false
        val outputStream = contentResolver.openOutputStream(document.uri) ?: return false
        writer = PrintWriter(outputStream)
        writer.println("timestamp,provider,longitude,latitude,altitude,bearing,speed,accuracy")
        writer.flush()
        return true
    }

    override fun split() {
        writer.println()
    }

    private lateinit var writer: PrintWriter
}
