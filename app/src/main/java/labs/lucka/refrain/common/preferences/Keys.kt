package labs.lucka.refrain.common.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    object Filter {
        val Accuracy = floatPreferencesKey("preference.filter.accuracy")
    }

    object Interval {
        val Time = longPreferencesKey("preference.interval.time")
        val Distance = floatPreferencesKey("preference.interval.distance")
    }

    object OutputFormat {
        val CSV = booleanPreferencesKey("preference.outputFormat.csv")
        val GPX = booleanPreferencesKey("preference.outputFormat.gpx")
        val KML = booleanPreferencesKey("preference.outputFormat.kml")
    }

    object Split {
        val Time = longPreferencesKey("preference.split.time")
        val Distance = floatPreferencesKey("preference.split.distance")
    }

    val OutputPath = stringPreferencesKey("preference.outputPath")
    val Provider = stringPreferencesKey("preference.provider")
}

