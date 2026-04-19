package com.ivarna.truvalt.presentation.ui.vault

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ivarna.truvalt.core.crypto.TotpGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private data class ItemTypeStyle(
    val icon: ImageVector,
    val iconColor: Color
)

private data class WebsiteIconRequest(
    val imageRequest: ImageRequest?,
)

@Composable
private fun itemTypeStyle(type: String): ItemTypeStyle {
    val palette = rememberVaultPalette()
    return when (type) {
        "login" -> ItemTypeStyle(Icons.Default.AccountCircle, palette.brand)
        "passkey" -> ItemTypeStyle(Icons.Default.Key, palette.brandStrong)
        "passphrase" -> ItemTypeStyle(Icons.Default.VpnKey, palette.brandStrong)
        "secure_note" -> ItemTypeStyle(Icons.Default.Description, palette.healthAccent)
        "security_code" -> ItemTypeStyle(Icons.Default.Shield, palette.brandStrong)
        "credit_card" -> ItemTypeStyle(Icons.Default.CreditCard, palette.body)
        "identity" -> ItemTypeStyle(Icons.Default.Person, palette.brand)
        else -> ItemTypeStyle(Icons.Default.Cloud, palette.brand)
    }
}

@Composable
fun VaultItemCard(
    item: VaultItemUi,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = rememberVaultPalette()
    val typeStyle = itemTypeStyle(item.type)
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val websiteIconRequest = remember(item.url, context) { buildWebsiteIconRequest(context, item.url) }
    
    var totpCode by remember { mutableStateOf<String?>(null) }
    var secondsRemaining by remember { mutableIntStateOf(30) }

    if (!item.totpSeed.isNullOrBlank()) {
        LaunchedEffect(item.totpSeed) {
            while (isActive) {
                val epochSeconds = System.currentTimeMillis() / 1000L
                val secondsElapsed = (epochSeconds % 30).toInt()
                secondsRemaining = 30 - secondsElapsed
                totpCode = try {
                    TotpGenerator.generate(item.totpSeed)
                } catch (e: Exception) { null }
                delay(1000L)
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Tile
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(palette.iconTileSurface, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (websiteIconRequest.imageRequest != null) {
                        SubcomposeAsyncImage(
                            model = websiteIconRequest.imageRequest,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = typeStyle.iconColor
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = typeStyle.icon,
                                    contentDescription = null,
                                    tint = typeStyle.iconColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        )
                    } else {
                        Icon(
                            imageVector = typeStyle.icon,
                            contentDescription = null,
                            tint = typeStyle.iconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = palette.title,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.isFavorite) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        palette.brand.copy(alpha = 0.1f),
                                        RoundedCornerShape(999.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FAV",
                                    color = palette.brand,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Primary Data (Username or Subtitle)
                    val primaryData = item.username.ifBlank { item.subtitle }
                    if (primaryData.isNotBlank()) {
                        Text(
                            text = primaryData,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.body
                        )
                    }
                    
                    // Secondary Data (URL if available and not used as subtitle)
                    if (item.url.isNotBlank() && item.url != primaryData) {
                        Text(
                            text = item.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.muted
                        )
                    }
                }

                // Copy Action
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.background(palette.mutedSurface.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = palette.brand,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2FA / TOTP Section (Aggressive white space per DESIGN.md)
            if (totpCode != null) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.mutedSurface.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "2FA CODE",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.muted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${totpCode!!.substring(0, 3)} ${totpCode!!.substring(3)}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            ),
                            color = palette.brand,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(totpCode!!)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy 2FA",
                                tint = palette.brand,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        // Small Progress indicator
                        val progress = secondsRemaining.toFloat() / 30f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.width(32.dp).height(2.dp),
                            color = if (secondsRemaining > 5) palette.brand else MaterialTheme.colorScheme.error,
                            trackColor = palette.mutedSurface
                        )
                    }
                }
            }
        }
    }
}

private fun buildWebsiteIconRequest(
    context: android.content.Context,
    websiteUrl: String
): WebsiteIconRequest {
    val trimmed = websiteUrl.trim()
    if (trimmed.isBlank()) return WebsiteIconRequest(null)

    val host = runCatching {
        val normalizedUrl = when {
            trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) -> trimmed
            else -> "https://$trimmed"
        }
        Uri.parse(normalizedUrl).host?.removePrefix("www.")?.takeIf { it.isNotBlank() }
    }.getOrNull().orEmpty()

    if (host.isBlank()) return WebsiteIconRequest(null)

    val faviconUrl = "https://www.google.com/s2/favicons?domain=${Uri.encode(host)}&sz=128"
    val request = ImageRequest.Builder(context)
        .data(faviconUrl)
        .memoryCacheKey("favicon:$host")
        .diskCacheKey("favicon:$host")
        .crossfade(true)
        .networkCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    return WebsiteIconRequest(request)
}
