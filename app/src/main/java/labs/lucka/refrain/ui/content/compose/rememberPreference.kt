package labs.lucka.refrain.ui.content.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import labs.lucka.refrain.common.preferencesDataStore

@Composable
fun <T> rememberPreference(key: Preferences.Key<T>, defaultValue: T): MutableState<T> {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = remember { context.preferencesDataStore.data.map { it[key] ?: defaultValue } }
        .collectAsState(initial = defaultValue)
    return remember {
        object : MutableState<T> {
            override var value: T
                get() = state.value
                set(value) {
                    scope.launch {
                        context.preferencesDataStore.edit {
                            it[key] = value
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}

@Composable
inline fun <reified T: Enum<T>> rememberPreference(key: Preferences.Key<Int>, defaultValue: T): MutableState<T> {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = remember { context.preferencesDataStore.data.map { it[key] ?: defaultValue.ordinal } }
        .collectAsState(initial = defaultValue.ordinal)
    return remember {
        object : MutableState<T> {
            override var value: T
                get() {
                    if (state.value < 0) {
                        return defaultValue
                    }
                    val values = enumValues<T>()
                    if (state.value >= values.size) {
                        return defaultValue
                    }
                    return values[state.value]
                }
                set(value) {
                    scope.launch {
                        context.preferencesDataStore.edit {
                            it[key] = value.ordinal
                        }
                    }
                }

            override fun component1() = value
            override fun component2(): (T) -> Unit = { value = it }
        }
    }
}