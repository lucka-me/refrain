package labs.lucka.refrain.service.appender

import android.content.ContentResolver
import android.location.Location
import androidx.documentfile.provider.DocumentFile
import java.io.PrintWriter
import java.time.ZonedDateTime

class KMLFileAppender : FileAppender {
    override fun append(location: Location) {
        writer.println("""          ${location.longitude},${location.latitude},${location.altitude}""")
    }

    override fun finish() {
        writer.println("""        </coordinates>""")
        writer.println("""      </LineString>""")
        writer.println("""    </Placemark>""")
        writer.println("""  </Document>""")
        writer.println("""</kml>""")
        writer.flush()
        writer.close()
    }

    override fun prepare(contentResolver: ContentResolver, tree: DocumentFile, dateTime: ZonedDateTime) : Boolean {
        val timestamp = dateTime.toInstant().toEpochMilli()
        val document = tree
            .createFile("application/vnd.google-earth.kml+xml", "$timestamp") ?: return false
        val outputStream = contentResolver.openOutputStream(document.uri) ?: return false
        writer = PrintWriter(outputStream)
        writer.println("""<?xml version="1.0" encoding="UTF-8"?>""")
        writer.println("""<kml xmlns="http://www.opengis.net/kml/2.2">""")
        writer.println("""  <Document>""")
        writer.println("""    <name>$timestamp.kml</name>""")
        writer.println("""    <Placemark>""")
        writer.println("""      <LineString>""")
        writer.println("""        <coordinates>""")
        writer.flush()
        return true
    }

    override fun split() {
        writer.println("""        </coordinates>""")
        writer.println("""      </LineString>""")
        writer.println("""    </Placemark>""")
        writer.println("""    <Placemark>""")
        writer.println("""      <LineString>""")
        writer.println("""        <coordinates>""")
    }

    private lateinit var writer: PrintWriter
}
