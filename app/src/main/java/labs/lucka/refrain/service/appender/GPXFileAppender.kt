package labs.lucka.refrain.service.appender

import android.content.ContentResolver
import android.location.Location
import androidx.documentfile.provider.DocumentFile
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GPXFileAppender : FileAppender {
    override fun append(location: Location) {
        val formattedTime = LocalDateTime
            .ofInstant(Date(location.time).toInstant(), ZoneId.systemDefault())
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        writer.println("""      <trkpt lat="${location.latitude}" lon="${location.longitude}">""")
        writer.println("""        <ele>${location.altitude}</ele>""")
        writer.println("""        <time>$formattedTime</time>""")
        writer.println("""      </trkpt>""")
    }

    override fun finish() {
        writer.println("""    </trkseg>""")
        writer.println("""  </trk>""")
        writer.println("""</gpx>""")
        writer.flush()
        writer.close()
    }

    override fun prepare(contentResolver: ContentResolver, tree: DocumentFile, dateTime: ZonedDateTime) : Boolean {
        val timestamp = dateTime.toInstant().toEpochMilli()
        // The MIME type will not be recognized correctly
        val document = tree
            .createFile("application/gpx+xml", "$timestamp.gpx") ?: return false
        val outputStream = contentResolver.openOutputStream(document.uri) ?: return false
        writer = PrintWriter(outputStream)
        writer.println("""<?xml version="1.0" encoding="UTF-8"?>""")
        writer.println("""<gpx xmlns="http://www.topografix.com/GPX/1/1">""")
        writer.println("""  <metadata>""")
        writer.println("""    <time>${dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}</time>""")
        writer.println("""  </metadata>""")
        writer.println("""  <trk>""")
        writer.println("""    <trkseg>""")
        writer.flush()
        return true
    }

    override fun split() {
        writer.println("""    </trkseg>""")
        writer.println("""    <trkseg>""")
    }

    private lateinit var writer: PrintWriter
}
