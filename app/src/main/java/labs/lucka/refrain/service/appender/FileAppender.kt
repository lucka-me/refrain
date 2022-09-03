package labs.lucka.refrain.service.appender

import android.content.ContentResolver
import android.location.Location
import androidx.documentfile.provider.DocumentFile
import java.time.ZonedDateTime

interface FileAppender {

    enum class FileType {
        CSV, GPX, KML
    }

    class Builder(private val type: FileType) {
        fun build() : FileAppender {
            return when (type) {
                FileType.CSV -> CSVFileAppender()
                FileType.GPX -> GPXFileAppender()
                FileType.KML -> KMLFileAppender()
            }
        }
    }

    fun append(location: Location)
    fun finish()
    fun prepare(contentResolver: ContentResolver, tree: DocumentFile, dateTime: ZonedDateTime): Boolean
}