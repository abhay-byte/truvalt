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
    primary = Color(0xFF0D7377),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF9EF1ED),
    onPrimaryContainer = Color(0xFF002021),
    secondary = Color(0xFF4A6364),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCE8E9),
    onSecondaryContainer = Color(0xFF062021),
    tertiary = Color(0xFF4B607C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFD3E4FF),
    onTertiaryContainer = Color(0xFF041C35),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFAFDFC),
    onBackground = Color(0xFF191C1C),
    surface = Color(0xFFFAFDFC),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E4),
    onSurfaceVariant = Color(0xFF3F4948),
    outline = Color(0xFF6F7978),
    outlineVariant = Color(0xFFBEC9C8)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CD9E4),
    onPrimary = Color(0xFF003739),
    primaryContainer = Color(0xFF004F50),
    onPrimaryContainer = Color(0xFF9EF1ED),
    secondary = Color(0xFFB0CCCD),
    onSecondary = Color(0xFF1C3536),
    secondaryContainer = Color(0xFF324B4C),
    onSecondaryContainer = Color(0xFFB0CCCD),
    tertiary = Color(0xFFB4C8EA),
    onTertiary = Color(0xFF1C314B),
    tertiaryContainer = Color(0xFF334863),
    onTertiaryContainer = Color(0xFFD3E4FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFB4AB),
    background = Color(0xFF191C1C),
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF191C1C),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF3F4948),
    onSurfaceVariant = Color(0xFFBEC9C8),
    outline = Color(0xFF899392),
    outlineVariant = Color(0xFF3F4948)
)

private val AmoledDarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CD9E4),
    onPrimary = Color(0xFF003739),
    primaryContainer = Color(0xFF004F50),
    onPrimaryContainer = Color(0xFF9EF1ED),
    secondary = Color(0xFFB0CCCD),
    onSecondary = Color(0xFF1C3536),
    secondaryContainer = Color(0xFF324B4C),
    onSecondaryContainer = Color(0xFFB0CCCD),
    tertiary = Color(0xFFB4C8EA),
    onTertiary = Color(0xFF1C314B),
    tertiaryContainer = Color(0xFF334863),
    onTertiaryContainer = Color(0xFFD3E4FF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFB4AB),
    background = Color(0xFF000000),
    onBackground = Color(0xFFE1E3E3),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFE1E3E3),
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFFBEC9C8),
    outline = Color(0xFF899392),
    outlineVariant = Color(0xFF3F4948)
)

@Composable
fun TruvaltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
