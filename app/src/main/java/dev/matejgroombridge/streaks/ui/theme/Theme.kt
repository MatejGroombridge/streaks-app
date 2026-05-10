package dev.matejgroombridge.streaks.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.matejgroombridge.streaks.data.ThemeMode

val StreakOrange = Color(0xFFFF9F43)
val StreakOrangeDeep = Color(0xFFE86F00)
val StreakOrangeSoft = Color(0xFFFFE2C2)
val StreakOrangeDark = Color(0xFF5A3518)

private val LightScheme: ColorScheme = lightColorScheme(
    primary = StreakOrangeDeep,
    onPrimary = Color.White,
    primaryContainer = StreakOrangeSoft,
    onPrimaryContainer = Color(0xFF3A210B),
    secondary = StreakOrangeDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE9D6),
    onSecondaryContainer = Color(0xFF3A210B),
    tertiary = StreakOrange,
    background = Color(0xFFFFFBF7),
    onBackground = Color(0xFF211A15),
    surface = Color(0xFFFFFBF7),
    onSurface = Color(0xFF211A15),
    surfaceVariant = Color(0xFFF4DED0),
    onSurfaceVariant = Color(0xFF55443A),
    surfaceContainer = Color(0xFFFFF0E3),
)

private val DarkScheme: ColorScheme = darkColorScheme(
    primary = StreakOrange,
    onPrimary = Color(0xFF3A210B),
    primaryContainer = StreakOrangeDark,
    onPrimaryContainer = Color(0xFFFFE2C2),
    secondary = StreakOrange,
    onSecondary = Color(0xFF3A210B),
    secondaryContainer = Color(0xFF4A2E16),
    onSecondaryContainer = Color(0xFFFFE2C2),
    tertiary = StreakOrangeSoft,
    background = Color(0xFF17120F),
    onBackground = Color(0xFFF4E7DE),
    surface = Color(0xFF17120F),
    onSurface = Color(0xFFF4E7DE),
    surfaceVariant = Color(0xFF55443A),
    onSurfaceVariant = Color(0xFFD8C2B3),
    surfaceContainer = Color(0xFF231A15),
)

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.System,
    amoled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isDark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val context = LocalContext.current
    val baseColorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDark) DarkScheme else LightScheme
    }
    val colorScheme = if (isDark && amoled) {
        baseColorScheme.copy(background = Color.Black, surface = Color.Black)
    } else {
        baseColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            val barsAreLight = colorScheme.background.luminance() > 0.5f
            controller.isAppearanceLightStatusBars = barsAreLight
            controller.isAppearanceLightNavigationBars = barsAreLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
