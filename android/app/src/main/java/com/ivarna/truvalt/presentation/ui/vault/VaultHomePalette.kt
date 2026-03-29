package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

data class VaultHomePalette(
    val background: Color,
    val backgroundAccent: Color,
    val heroGlow: Color,
    val headerSurface: Color,
    val searchSurface: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val mutedSurface: Color,
    val iconTileSurface: Color,
    val title: Color,
    val body: Color,
    val muted: Color,
    val brand: Color,
    val brandStrong: Color,
    val chipSelectedSurface: Color,
    val chipSelectedText: Color,
    val chipIdleSurface: Color,
    val chipIdleText: Color,
    val fabGradientStart: Color,
    val fabGradientEnd: Color,
    val healthAccent: Color,
    val healthTrack: Color,
    val healthRing: Color,
    val navSurface: Color,
    val navBorder: Color,
    val navSelectedSurface: Color
)

@Composable
fun rememberVaultPalette(): VaultHomePalette {
    val scheme = MaterialTheme.colorScheme

    return remember(scheme) {
        VaultHomePalette(
            background = scheme.background,
            backgroundAccent = scheme.surface,
            heroGlow = scheme.primary.copy(alpha = 0.15f),
            headerSurface = scheme.surfaceContainerLow,
            searchSurface = scheme.surfaceContainerLowest,
            cardSurface = scheme.surfaceContainerLow,
            cardBorder = scheme.outlineVariant,
            mutedSurface = scheme.surfaceContainerHighest,
            iconTileSurface = scheme.surfaceContainerHighest,
            title = scheme.onSurface,
            body = scheme.onSurfaceVariant,
            muted = scheme.outline,
            brand = scheme.primary,
            brandStrong = scheme.primaryContainer,
            chipSelectedSurface = scheme.primaryContainer,
            chipSelectedText = scheme.onPrimaryContainer,
            chipIdleSurface = scheme.surfaceContainerHighest,
            chipIdleText = scheme.onSurfaceVariant,
            fabGradientStart = scheme.primary,
            fabGradientEnd = scheme.primaryContainer,
            healthAccent = scheme.tertiary,
            healthTrack = scheme.surfaceContainerHighest,
            healthRing = scheme.tertiaryContainer,
            navSurface = scheme.surfaceContainerLow,
            navBorder = scheme.outlineVariant,
            navSelectedSurface = scheme.surfaceContainerHighest
        )
    }
}
