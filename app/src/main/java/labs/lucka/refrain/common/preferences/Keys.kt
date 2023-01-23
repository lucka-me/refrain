package labs.lucka.refrain.common.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class Keys {

    class FilterKeys internal constructor() {
        val accuracy = floatPreferencesKey("preference.filter.accuracy")
    }

    class IntervalKeys internal constructor () {
        val time = longPreferencesKey("preference.interval.time")
        val distance = floatPreferencesKey("preference.interval.distance")
    }

    class OutputFormatKeys internal constructor() {
        val csv = booleanPreferencesKey("preference.outputFormat.csv")
        val gpx = booleanPreferencesKey("preference.outputFormat.gpx")
        val kml = booleanPreferencesKey("preference.outputFormat.kml")
    }

    class SplitKeys internal  constructor() {
        val time = longPreferencesKey("preference.split.time")
        val distance = floatPreferencesKey("preference.split.distance")
    }

    companion object {
        val filter = FilterKeys()
        val interval = IntervalKeys()
        val outputFormat = OutputFormatKeys()
        val split = SplitKeys()
        val outputPath = stringPreferencesKey("preference.outputPath")
        val provider = stringPreferencesKey("preference.provider")
    }
}

