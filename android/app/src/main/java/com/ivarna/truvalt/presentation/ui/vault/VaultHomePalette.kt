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
    val isDark = scheme.background.luminance() < 0.5f

    return remember(isDark) {
        if (isDark) {
            VaultHomePalette(
                background = Color(0xFF0B1326),
                backgroundAccent = Color(0xFF0F1C36),
                heroGlow = Color(0x332B61D6),
                headerSurface = Color(0xCC0D162B),
                searchSurface = Color(0xFF141E35),
                cardSurface = Color(0xFF161F34),
                cardBorder = Color(0x1FFFFFFF),
                mutedSurface = Color(0xFF242E46),
                iconTileSurface = Color(0xFF313B58),
                title = Color(0xFFE7EEFF),
                body = Color(0xFFD5DCEE),
                muted = Color(0xFFA1AAC2),
                brand = Color(0xFFADC6FF),
                brandStrong = Color(0xFF76A4FF),
                chipSelectedSurface = Color(0xFFADC6FF),
                chipSelectedText = Color(0xFF112754),
                chipIdleSurface = Color(0xFF242E46),
                chipIdleText = Color(0xFFC7CFDF),
                fabGradientStart = Color(0xFF7AA7FF),
                fabGradientEnd = Color(0xFF4E78D9),
                healthAccent = Color(0xFF4EDEA3),
                healthTrack = Color(0xFF2C3651),
                healthRing = Color(0xFF1A6652),
                navSurface = Color(0xE60C152A),
                navBorder = Color(0x24E7EEFF),
                navSelectedSurface = Color(0xFF1A2744)
            )
        } else {
            VaultHomePalette(
                background = Color(0xFFF2F6FF),
                backgroundAccent = Color(0xFFE8F0FF),
                heroGlow = Color(0x2A4D89FF),
                headerSurface = Color(0xECF6F9FF),
                searchSurface = Color(0xFFFFFFFF),
                cardSurface = Color(0xFFFFFFFF),
                cardBorder = Color(0x140F274A),
                mutedSurface = Color(0xFFE6EDFA),
                iconTileSurface = Color(0xFFDEE7F8),
                title = Color(0xFF0F213F),
                body = Color(0xFF203453),
                muted = Color(0xFF697A99),
                brand = Color(0xFF2D63D4),
                brandStrong = Color(0xFF1846A3),
                chipSelectedSurface = Color(0xFFD5E3FF),
                chipSelectedText = Color(0xFF123676),
                chipIdleSurface = Color(0xFFE7EDF8),
                chipIdleText = Color(0xFF536684),
                fabGradientStart = Color(0xFF5E94FF),
                fabGradientEnd = Color(0xFF2D63D4),
                healthAccent = Color(0xFF00A878),
                healthTrack = Color(0xFFDCE7F6),
                healthRing = Color(0xFFB7E8D6),
                navSurface = Color(0xF8F7FAFF),
                navBorder = Color(0x140F274A),
                navSelectedSurface = Color(0xFFDDE8FF)
            )
        }
    }
}
