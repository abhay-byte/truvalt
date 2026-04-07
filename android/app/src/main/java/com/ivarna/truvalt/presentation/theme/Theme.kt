package com.ivarna.truvalt.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5850BD),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF958DFF),
    onPrimaryContainer = Color(0xFF150066),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    error = Color(0xFFA8364B),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFCF8FE),
    onBackground = Color(0xFF33313A),
    surface = Color(0xFFFCF8FE),
    onSurface = Color(0xFF33313A),
    surfaceVariant = Color(0xFFF6F2FA),
    onSurfaceVariant = Color(0xFF605E68),
    surfaceContainerLow = Color(0xFFF6F2FA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFE5E1ED),
    surfaceBright = Color(0xFFFCF8FE),
    outline = Color(0xFFB4B0BC),
    outlineVariant = Color(0xFFEBE6F0) // 15% opacity equivalent of on-surface on low container
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7AA7FF),
    onPrimary = Color(0xFF0F1C36),
    primaryContainer = Color(0xFF4E78D9),
    onPrimaryContainer = Color(0xFFE7EEFF),
    secondary = Color(0xFFADC6FF),
    onSecondary = Color(0xFF112754),
    secondaryContainer = Color(0xFF313B58),
    onSecondaryContainer = Color(0xFFE7EEFF),
    tertiary = Color(0xFF4EDEA3),
    onTertiary = Color(0xFF0F1C36),
    tertiaryContainer = Color(0xFF1A6652),
    onTertiaryContainer = Color(0xFFD5DCEE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0B1326),
    onBackground = Color(0xFFE7EEFF),
    surface = Color(0xFF0B1326),
    onSurface = Color(0xFFE7EEFF),
    surfaceVariant = Color(0xFF242E46),
    onSurfaceVariant = Color(0xFFC1C9E0),
    surfaceContainerLow = Color(0xFF0F1C36),
    surfaceContainerLowest = Color(0xFF141E35),
    surfaceContainerHighest = Color(0xFF161F34),
    surfaceBright = Color(0xFF0B1326),
    outline = Color(0xFF8E99B3),
    outlineVariant = Color(0xFF1C2744) // Ghost border equivalent for dark mode
)

private val AmoledDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC4C0FF),
    onPrimary = Color(0xFF29208D),
    primaryContainer = Color(0xFF4037A3),
    onPrimaryContainer = Color(0xFFE0E0FF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF000000),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFFE1E1E6),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F)
)

@Composable
fun TruvaltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    amoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme && amoled -> AmoledDarkColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
