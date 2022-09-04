package labs.lucka.refrain.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Mikan40,
    primaryContainer = Mikan90,
    onPrimaryContainer = Mikan10,
    secondary = Aqua40,
    secondaryContainer = Aqua90,
    onSecondaryContainer = Aqua10,
    tertiary = Pink40
)

private val DarkColorScheme = darkColorScheme(
    primary = Mikan80,
    onPrimary = Mikan20,
    primaryContainer = Mikan30,
    onPrimaryContainer = Mikan90,
    secondary = Aqua80,
    onSecondary = Aqua20,
    secondaryContainer = Aqua30,
    onSecondaryContainer = Aqua90,
    tertiary = Pink80
)

@Composable
fun RefrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}