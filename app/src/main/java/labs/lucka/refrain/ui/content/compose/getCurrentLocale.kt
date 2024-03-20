package labs.lucka.refrain.ui.content.compose

import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

@Composable
@ReadOnlyComposable
fun getCurrentLocale(): Locale {
    return LocalConfiguration.current.locales.get(0) ?: LocaleList.getDefault()[0]
}