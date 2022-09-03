package labs.lucka.refrain.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore("preferences")